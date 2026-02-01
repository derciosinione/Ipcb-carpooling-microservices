package pt.ipcb.carpooling.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RideController {

    @GetMapping("/ride/{id}")
    public String rideDetails(@PathVariable Long id, Model model) {
        // In a real app, retrieve ride details by ID
        return "ride-details";
    }

    @GetMapping("/driver/{id}")
    public String driverDetails(@PathVariable Long id, Model model) {
        // In a real app, retrieve driver details by ID
        return "driver-details";
    }
}
