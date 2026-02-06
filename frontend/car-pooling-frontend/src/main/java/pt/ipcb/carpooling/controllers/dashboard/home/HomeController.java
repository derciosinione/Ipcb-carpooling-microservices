package pt.ipcb.carpooling.controllers.dashboard.home;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.MetricsDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.trips.TripsDashboardService;

import java.util.List;

@Controller("dashboardHomeController")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class HomeController {
    private final TripsDashboardService tripsDashboardService;

    @GetMapping
    public String dashboardHome(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (isAdmin(user)) {
            return "redirect:/dashboard/admin";
        }

        List<TripDto.TripResponse> driverTrips = tripsDashboardService.getTripsByDriver(user.getId());
        List<TripDto.TripResponse> passengerTrips = tripsDashboardService.getTripsByPassenger(user.getId());

        MetricsDto.MetricsResponse driverMetrics = tripsDashboardService.getMetrics("DRIVER");
        MetricsDto.MetricsResponse passengerMetrics = tripsDashboardService.getMetrics("PASSENGER");

        model.addAttribute("driverTotalTrips",
                driverMetrics != null ? driverMetrics.getTotalTrips() : driverTrips.size());
        model.addAttribute("passengerTotalTrips",
                passengerMetrics != null ? passengerMetrics.getTotalTrips() : passengerTrips.size());
        model.addAttribute("driverUpcomingTrips", tripsDashboardService.filterUpcoming(driverTrips));
        model.addAttribute("passengerUpcomingTrips", tripsDashboardService.filterUpcoming(passengerTrips));

        model.addAttribute("driverTotalEarnings",
                driverMetrics != null ? driverMetrics.getTotalEarnings() : tripsDashboardService.sumTotalCost(driverTrips));
        model.addAttribute("passengerTotalSpend", passengerMetrics != null ? passengerMetrics.getTotalSpend()
                : tripsDashboardService.sumCostPerSeat(passengerTrips));
        model.addAttribute("driverTotalKm",
                driverMetrics != null ? driverMetrics.getTotalKm() : java.math.BigDecimal.ZERO);
        model.addAttribute("passengerTotalKm",
                passengerMetrics != null ? passengerMetrics.getTotalKm() : java.math.BigDecimal.ZERO);
        return "dashboard/home";
    }

    private boolean isAdmin(AuthDto.LoginResponse user) {
        return user.getRoles() != null
                && user.getRoles().stream().anyMatch(r -> "Admin".equalsIgnoreCase(r) || "ADMIN".equalsIgnoreCase(r));
    }
}
