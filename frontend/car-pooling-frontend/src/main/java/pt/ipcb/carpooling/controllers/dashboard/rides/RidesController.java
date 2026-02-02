package pt.ipcb.carpooling.controllers.dashboard.rides;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.PassengerTripDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.trips.TripsDashboardService;
import pt.ipcb.carpooling.clients.TripsClient;

import java.util.List;
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class RidesController {
    private final TripsDashboardService tripsDashboardService;
    private final TripsClient tripsClient;

    @GetMapping("/rides")
    public String rides(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        List<TripDto.TripResponse> trips = tripsClient.getTripsByDriver(user.getId());
        model.addAttribute("driverUpcomingTrips", tripsDashboardService.filterUpcoming(trips));
        model.addAttribute("driverHistoryTrips", tripsDashboardService.filterHistory(trips));

        List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByPassenger(user.getId());
        List<PassengerTripDto> passengerTrips = tripsDashboardService.buildPassengerTrips(bookings);
        model.addAttribute("passengerUpcomingTrips", tripsDashboardService.filterUpcomingPassengerTrips(passengerTrips));
        model.addAttribute("passengerHistoryTrips", tripsDashboardService.filterHistoryPassengerTrips(passengerTrips));

        return "dashboard/rides";
    }
}
