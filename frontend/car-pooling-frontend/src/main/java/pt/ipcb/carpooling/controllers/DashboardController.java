package pt.ipcb.carpooling.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.ExpenseDto;
import pt.ipcb.carpooling.dto.PublishRideForm;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.dto.VehicleDto;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final VehicleClient vehicleClient;
    private final TripsClient tripsClient;

    @GetMapping
    public String dashboardHome() {
        return "dashboard/home";
    }

    @GetMapping("/rides")
    public String rides(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        try {
            List<TripDto.TripResponse> trips = tripsClient.getTripsByDriver(user.getId());
            model.addAttribute("driverUpcomingTrips", filterUpcoming(trips));
            model.addAttribute("driverHistoryTrips", filterHistory(trips));
        } catch (Exception e) {
            log.error("Error loading trips for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("driverUpcomingTrips", List.of());
            model.addAttribute("driverHistoryTrips", List.of());
            model.addAttribute("error", "Erro ao carregar as suas viagens.");
        }

        try {
            List<TripDto.TripResponse> passengerTrips = tripsClient.getTripsByPassenger(user.getId());
            model.addAttribute("passengerUpcomingTrips", filterUpcoming(passengerTrips));
            model.addAttribute("passengerHistoryTrips", filterHistory(passengerTrips));
        } catch (Exception e) {
            log.error("Error loading passenger trips for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("passengerUpcomingTrips", List.of());
            model.addAttribute("passengerHistoryTrips", List.of());
        }

        return "dashboard/rides";
    }

    @GetMapping("/settings")
    public String settings() {
        return "dashboard/settings";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");

        try {
            List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getVehiclesByOwner(user.getId());
            model.addAttribute("vehicles", vehicles);
        } catch (Exception e) {
            log.error("Error loading vehicles for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("error", "Erro ao carregar os seus veículos. Por favor, tente mais tarde.");
            model.addAttribute("vehicles", List.of());
        }

        try {
            List<VehicleDto.BrandResponse> brands = vehicleClient.getAllBrands();
            model.addAttribute("brands", brands);
        } catch (Exception e) {
            log.error("Error loading car brands: {}", e.getMessage());
            if (!model.containsAttribute("error")) {
                model.addAttribute("error", "Erro ao carregar dados do formulário.");
            }
            model.addAttribute("brands", List.of());
        }

        return "dashboard/vehicles";
    }

    @PostMapping("/vehicles")
    public String createVehicle(VehicleDto.VehicleRequest request, HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");

        try {
            request.setOwnerId(user.getId());
            // Default seats if not provided (safety)
            if (request.getSeats() == null)
                request.setSeats(4);

            vehicleClient.createVehicle(request);
            redirectAttributes.addFlashAttribute("success", "Veículo adicionado com sucesso!");
        } catch (Exception e) {
            log.error("Error creating vehicle: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erro ao adicionar veículo: " + e.getMessage());
        }

        return "redirect:/dashboard/vehicles";
    }

    @GetMapping("/publish-ride")
    public String publishRide(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        try {
            List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getVehiclesByOwner(user.getId());
            model.addAttribute("vehicles", vehicles);
        } catch (Exception e) {
            log.error("Error loading vehicles for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("vehicles", List.of());
            model.addAttribute("error", "Erro ao carregar os seus veículos. Por favor, tente mais tarde.");
        }

        return "dashboard/publish-ride";
    }

    @PostMapping("/publish-ride")
    public String publishRideSubmit(PublishRideForm form, HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        try {
            LocalDate date = LocalDate.parse(form.getDate());
            LocalTime time = LocalTime.parse(form.getTime());
            LocalDateTime departureTime = LocalDateTime.of(date, time);

            TripDto.CreateTripRequest request = TripDto.CreateTripRequest.builder()
                    .driverId(user.getId())
                    .vehicleId(form.getVehicleId())
                    .origin(form.getOrigin())
                    .destination(form.getDestination())
                    .description(form.getDescription())
                    .departureTime(departureTime)
                    .availableSeats(form.getSeats())
                    .build();

            tripsClient.createTrip(request);
            redirectAttributes.addFlashAttribute("success", "Boleia publicada com sucesso!");
            return "redirect:/dashboard/rides";
        } catch (Exception e) {
            log.error("Error creating trip: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erro ao publicar boleia. Verifique os dados.");
            return "redirect:/dashboard/publish-ride";
        }
    }

    @GetMapping("/messages")
    public String messages() {
        return "dashboard/messages";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "dashboard/notifications";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "1") Integer seats,
            Model model) {
        if (origin != null && destination != null) {
            try {
                List<TripDto.TripResponse> results = tripsClient.searchTrips(origin, destination, seats);
                model.addAttribute("results", results);
            } catch (Exception e) {
                log.error("Error searching trips: {}", e.getMessage());
                model.addAttribute("results", List.of());
                model.addAttribute("error", "Erro ao procurar viagens.");
            }
        } else {
            model.addAttribute("results", List.of());
        }
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);
        model.addAttribute("seats", seats);
        return "dashboard/search";
    }

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

        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("confirmedBookings", confirmedBookings);
        model.addAttribute("totalSeats", (trip.getAvailableSeats() != null ? trip.getAvailableSeats() : 0)
                + (trip.getConfirmedSeats() != null ? trip.getConfirmedSeats() : 0));

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

    private List<TripDto.TripResponse> filterUpcoming(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> !"FINISHED".equalsIgnoreCase(trip.getStatus())
                        && !"CANCELED".equalsIgnoreCase(trip.getStatus()))
                .collect(Collectors.toList());
    }

    private List<TripDto.TripResponse> filterHistory(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> "FINISHED".equalsIgnoreCase(trip.getStatus())
                        || "CANCELED".equalsIgnoreCase(trip.getStatus()))
                .collect(Collectors.toList());
    }
}
