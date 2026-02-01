package pt.ipcb.carpooling.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String dashboardHome() {
        return "dashboard/home";
    }

    @GetMapping("/rides")
    public String rides() {
        return "dashboard/rides";
    }

    @GetMapping("/settings")
    public String settings() {
        return "dashboard/settings";
    }

    @GetMapping("/vehicles")
    public String vehicles() {
        return "dashboard/vehicles";
    }

    @GetMapping("/publish-ride")
    public String publishRide() {
        return "dashboard/publish-ride";
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
    public String search() {
        return "dashboard/search";
    }

    @GetMapping("/ride/{id}")
    public String rideDetails() {
        return "dashboard/ride-details";
    }
}
