package pt.ipcb.carpooling.controllers.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.ExpenseDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.dto.UserDto;
import pt.ipcb.carpooling.dto.VehicleDto;
import pt.ipcb.carpooling.services.DashboardService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller("dashboardRideController")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class RideController {
    private final TripsClient tripsClient;
    private final VehicleClient vehicleClient;
    private final DashboardService dashboardService;

    @GetMapping("/ride/{id}")
    public String rideDetails(@PathVariable String id, Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        TripDto.TripResponse trip;
        try {
            trip = tripsClient.getTripById(id);
            model.addAttribute("trip", trip);
        } catch (Exception e) {
            log.error("Error loading trip {}: {}", id, e.getMessage());
            model.addAttribute("error", "Erro ao carregar a viagem.");
            model.addAttribute("isDriver", false);
            model.addAttribute("isCompleted", false);
            return "dashboard/ride-details";
        }

        boolean isDriver = Objects.equals(trip.getDriverId(), user.getId());
        model.addAttribute("isDriver", isDriver);
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("isCompleted", "FINISHED".equalsIgnoreCase(trip.getStatus()));

        List<BookingDto.BookingResponse> bookings;
        try {
            bookings = tripsClient.getBookingsByTrip(id);
        } catch (Exception e) {
            log.error("Error loading bookings for trip {}: {}", id, e.getMessage());
            bookings = List.of();
        }

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
            Map<String, UserDto.UserResponse> users = dashboardService.fetchUsersByIds(userIds.stream().toList());
            model.addAttribute("userNames", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> dashboardService.safeName(e.getValue()))));
            model.addAttribute("userInitials", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> dashboardService.initials(e.getValue()))));
        }

        try {
            List<ExpenseDto.ExpenseResponse> expenses = tripsClient.getExpensesByTrip(id);
            model.addAttribute("expenses", expenses);
            model.addAttribute("totalExpenses",
                    expenses.stream()
                            .map(ExpenseDto.ExpenseResponse::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        } catch (Exception e) {
            log.error("Error loading expenses for trip {}: {}", id, e.getMessage());
            model.addAttribute("expenses", List.of());
            model.addAttribute("totalExpenses", java.math.BigDecimal.ZERO);
        }

        try {
            List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();
            VehicleDto.VehicleResponse vehicle = vehicles.stream()
                    .filter(v -> Objects.equals(v.getId(), trip.getVehicleId()))
                    .findFirst()
                    .orElse(null);
            model.addAttribute("vehicle", vehicle);
        } catch (Exception e) {
            log.error("Error loading vehicles: {}", e.getMessage());
            model.addAttribute("vehicle", null);
        }

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
        try {
            tripsClient.payBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "Pagamento efetuado com sucesso!");
        } catch (Exception e) {
            log.error("Error paying booking {}: {}", bookingId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Não foi possível efetuar o pagamento.");
        }
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

        try {
            BookingDto.CreateBookingRequest request = new BookingDto.CreateBookingRequest();
            request.setTripId(tripId);
            request.setSeats(seats);
            request.setPassengerId(user.getId());
            tripsClient.createBooking(request);
            redirectAttributes.addFlashAttribute("success", "Reserva efetuada com sucesso!");
        } catch (Exception e) {
            log.error("Error creating booking for trip {}: {}", tripId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erro ao solicitar reserva.");
        }

        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/finish")
    public String finishTrip(@PathVariable String tripId, RedirectAttributes redirectAttributes) {
        try {
            tripsClient.updateTripStatus(tripId, new TripDto.UpdateTripStatusRequest("FINISHED"));
            redirectAttributes.addFlashAttribute("success", "Viagem concluida com sucesso.");
        } catch (Exception e) {
            log.error("Error finishing trip {}: {}", tripId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Nao foi possivel concluir a viagem.");
        }
        return "redirect:/dashboard/ride/" + tripId;
    }

    @PostMapping("/ride/{tripId}/start")
    public String startTrip(@PathVariable String tripId, RedirectAttributes redirectAttributes) {
        try {
            tripsClient.updateTripStatus(tripId, new TripDto.UpdateTripStatusRequest("STARTED"));
            redirectAttributes.addFlashAttribute("success", "Viagem iniciada com sucesso.");
        } catch (Exception e) {
            log.error("Error starting trip {}: {}", tripId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Nao foi possivel iniciar a viagem.");
        }
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
