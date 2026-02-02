package pt.ipcb.carpooling.controllers.dashboard.rides;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.RatingsClient;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.ExpenseDto;
import pt.ipcb.carpooling.dto.RatingDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.dto.UserDto;
import pt.ipcb.carpooling.dto.VehicleDto;
import pt.ipcb.carpooling.services.identity.IdentityDashboardService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("dashboardRideController")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class RideController {
    private final TripsClient tripsClient;
    private final VehicleClient vehicleClient;
    private final IdentityDashboardService identityDashboardService;
    private final RatingsClient ratingsClient;

    @GetMapping("/ride/{id}")
    public String rideDetails(@PathVariable String id, Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        TripDto.TripResponse trip = tripsClient.getTripById(id);
        model.addAttribute("trip", trip);

        boolean isDriver = Objects.equals(trip.getDriverId(), user.getId());
        model.addAttribute("isDriver", isDriver);
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("isCompleted", "FINISHED".equalsIgnoreCase(trip.getStatus()));

        List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByTrip(id);

        List<BookingDto.BookingResponse> pendingBookings = bookings.stream()
                .filter(b -> "PENDING".equalsIgnoreCase(b.getStatus()))
                .toList();
        List<BookingDto.BookingResponse> confirmedBookings = bookings.stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .toList();
        BookingDto.BookingResponse currentUserBooking = bookings.stream()
                .filter(b -> Objects.equals(b.getPassengerId(), user.getId()))
                .findFirst()
                .orElse(null);

        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("confirmedBookings", confirmedBookings);
        model.addAttribute("currentUserBooking", currentUserBooking);
        model.addAttribute("totalSeats", (trip.getAvailableSeats() != null ? trip.getAvailableSeats() : 0)
                + (trip.getConfirmedSeats() != null ? trip.getConfirmedSeats() : 0));

        Set<String> userIds = bookings.stream()
                .map(BookingDto.BookingResponse::getPassengerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (trip.getDriverId() != null) {
            userIds.add(trip.getDriverId());
        }
        if (!userIds.isEmpty()) {
            Map<String, UserDto.UserResponse> users = identityDashboardService.fetchUsersByIds(userIds.stream().toList());
            model.addAttribute("userNames", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.safeName(e.getValue()))));
            model.addAttribute("userInitials", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.initials(e.getValue()))));
        }

        List<ExpenseDto.ExpenseResponse> expenses = tripsClient.getExpensesByTrip(id);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalExpenses",
                expenses.stream()
                        .map(ExpenseDto.ExpenseResponse::getAmount)
                        .filter(Objects::nonNull)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

        List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();
        VehicleDto.VehicleResponse vehicle = vehicles.stream()
                .filter(v -> Objects.equals(v.getId(), trip.getVehicleId()))
                .findFirst()
                .orElse(null);
        model.addAttribute("vehicle", vehicle);

        return "dashboard/ride-details";
    }

    @PostMapping("/ride/{tripId}/bookings/{bookingId}/accept")
    public String acceptBooking(@PathVariable String tripId, @PathVariable String bookingId) {
        tripsClient.acceptBooking(bookingId);
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/bookings/{bookingId}/reject")
    public String rejectBooking(@PathVariable String tripId, @PathVariable String bookingId) {
        tripsClient.rejectBooking(bookingId);
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable String tripId, @PathVariable String bookingId) {
        tripsClient.cancelBooking(bookingId);
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/bookings/{bookingId}/pay")
    public String payBooking(@PathVariable String tripId, @PathVariable String bookingId,
            RedirectAttributes redirectAttributes) {
        tripsClient.payBooking(bookingId);
        redirectAttributes.addFlashAttribute("success", "Pagamento efetuado com sucesso!");
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/book")
    public String createBooking(@PathVariable String tripId,
            @RequestParam(defaultValue = "1") Integer seats,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        BookingDto.CreateBookingRequest request = new BookingDto.CreateBookingRequest();
        request.setTripId(tripId);
        request.setSeats(seats);
        request.setPassengerId(user.getId());
        tripsClient.createBooking(request);
        redirectAttributes.addFlashAttribute("success", "Reserva efetuada com sucesso!");

        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/finish")
    public String finishTrip(@PathVariable String tripId, RedirectAttributes redirectAttributes) {
        tripsClient.updateTripStatus(tripId, new TripDto.UpdateTripStatusRequest("FINISHED"));
        redirectAttributes.addFlashAttribute("success", "Viagem concluida com sucesso.");
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/start")
    public String startTrip(@PathVariable String tripId, RedirectAttributes redirectAttributes) {
        tripsClient.updateTripStatus(tripId, new TripDto.UpdateTripStatusRequest("STARTED"));
        redirectAttributes.addFlashAttribute("success", "Viagem iniciada com sucesso.");
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/rate-user")
    public String rateUser(@PathVariable String tripId,
            @RequestParam String targetUserId,
            @RequestParam String role,
            @RequestParam Integer stars,
            @RequestParam(required = false) String comment,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        TripDto.TripResponse trip = tripsClient.getTripById(tripId);
        if (!"FINISHED".equalsIgnoreCase(trip.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "A viagem ainda não foi concluída.");
            return "redirect:/dashboard/ride/" + tripId;
        }

        if ("driver".equalsIgnoreCase(role)) {
            // passenger rating driver
            if (!targetUserId.equals(trip.getDriverId())) {
                redirectAttributes.addFlashAttribute("error", "Condutor inválido para avaliação.");
                return "redirect:/dashboard/ride/" + tripId;
            }
            List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByTrip(tripId);
            boolean isConfirmedPassenger = bookings.stream()
                    .anyMatch(b -> user.getId().equals(b.getPassengerId())
                            && "CONFIRMED".equalsIgnoreCase(b.getStatus()));
            if (!isConfirmedPassenger) {
                redirectAttributes.addFlashAttribute("error", "Apenas passageiros confirmados podem avaliar o condutor.");
                return "redirect:/dashboard/ride/" + tripId;
            }
        } else {
            // driver rating passenger
            if (!user.getId().equals(trip.getDriverId())) {
                redirectAttributes.addFlashAttribute("error", "Apenas o condutor pode avaliar passageiros.");
                return "redirect:/dashboard/ride/" + tripId;
            }
            List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByTrip(tripId);
            boolean passengerConfirmed = bookings.stream()
                    .anyMatch(b -> targetUserId.equals(b.getPassengerId())
                            && "CONFIRMED".equalsIgnoreCase(b.getStatus()));
            if (!passengerConfirmed) {
                redirectAttributes.addFlashAttribute("error", "Passageiro inválido para avaliação.");
                return "redirect:/dashboard/ride/" + tripId;
            }
        }

        RatingDto.CreateRatingRequest request = new RatingDto.CreateRatingRequest();
        request.setRaterId(user.getId());
        request.setRatedUserId(targetUserId);
        request.setStars(stars);
        request.setComment(comment);
        ratingsClient.create(request);

        redirectAttributes.addFlashAttribute("success", "Avaliação enviada com sucesso.");

        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/expenses")
    public String addExpense(@PathVariable String tripId,
            @RequestParam String description,
            @RequestParam java.math.BigDecimal amount) {
        ExpenseDto.CreateExpenseRequest request = ExpenseDto.CreateExpenseRequest.builder()
                .tripId(tripId)
                .description(description)
                .amount(amount)
                .type("OTHER")
                .build();
        tripsClient.createExpense(request);
        return "redirect:/dashboard/ride/" + tripId + "?openExpenses=true";
    }
}
