package pt.ipcb.carpooling.controllers.dashboard;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.MetricsDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.DashboardService;

import java.util.List;

@Controller("dashboardHomeController")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final DashboardService dashboardService;

    @GetMapping
    public String dashboardHome(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        List<TripDto.TripResponse> driverTrips = dashboardService.safeGetTripsByDriver(user.getId());
        List<TripDto.TripResponse> passengerTrips = dashboardService.safeGetTripsByPassenger(user.getId());

        MetricsDto.MetricsResponse driverMetrics = dashboardService.safeGetMetrics("DRIVER");
        MetricsDto.MetricsResponse passengerMetrics = dashboardService.safeGetMetrics("PASSENGER");

        model.addAttribute("driverTotalTrips",
                driverMetrics != null ? driverMetrics.getTotalTrips() : driverTrips.size());
        model.addAttribute("passengerTotalTrips",
                passengerMetrics != null ? passengerMetrics.getTotalTrips() : passengerTrips.size());
        model.addAttribute("driverUpcomingTrips", dashboardService.filterUpcoming(driverTrips));
        model.addAttribute("passengerUpcomingTrips", dashboardService.filterUpcoming(passengerTrips));

        model.addAttribute("driverTotalEarnings",
                driverMetrics != null ? driverMetrics.getTotalEarnings() : dashboardService.sumTotalCost(driverTrips));
        model.addAttribute("passengerTotalSpend", passengerMetrics != null ? passengerMetrics.getTotalSpend()
                : dashboardService.sumCostPerSeat(passengerTrips));
        model.addAttribute("driverTotalKm",
                driverMetrics != null ? driverMetrics.getTotalKm() : java.math.BigDecimal.ZERO);
        model.addAttribute("passengerTotalKm",
                passengerMetrics != null ? passengerMetrics.getTotalKm() : java.math.BigDecimal.ZERO);
        return "dashboard/home";
    }
}