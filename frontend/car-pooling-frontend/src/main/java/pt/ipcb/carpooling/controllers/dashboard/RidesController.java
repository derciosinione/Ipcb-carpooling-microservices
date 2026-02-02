package pt.ipcb.carpooling.controllers.dashboard;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.PassengerTripDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.DashboardService;
import pt.ipcb.carpooling.clients.TripsClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class RidesController {
    private final DashboardService dashboardService;
    private final TripsClient tripsClient;

    @GetMapping("/rides")
    public String rides(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        try {
            List<TripDto.TripResponse> trips = tripsClient.getTripsByDriver(user.getId());
            model.addAttribute("driverUpcomingTrips", dashboardService.filterUpcoming(trips));
            model.addAttribute("driverHistoryTrips", dashboardService.filterHistory(trips));
        } catch (Exception e) {
            log.error("Error loading trips for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("driverUpcomingTrips", List.of());
            model.addAttribute("driverHistoryTrips", List.of());
            model.addAttribute("error", "Erro ao carregar as suas viagens.");
        }

        try {
            List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByPassenger(user.getId());
            List<PassengerTripDto> passengerTrips = dashboardService.buildPassengerTrips(bookings);
            model.addAttribute("passengerUpcomingTrips", dashboardService.filterUpcomingPassengerTrips(passengerTrips));
            model.addAttribute("passengerHistoryTrips", dashboardService.filterHistoryPassengerTrips(passengerTrips));
        } catch (Exception e) {
            log.error("Error loading passenger trips for user {}: {}", user.getId(), e.getMessage());
            model.addAttribute("passengerUpcomingTrips", List.of());
            model.addAttribute("passengerHistoryTrips", List.of());
        }

        return "dashboard/rides";
    }
}