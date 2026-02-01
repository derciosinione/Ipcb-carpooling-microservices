package pt.ipcb.carpooling.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.VehicleDto;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final VehicleClient vehicleClient;

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
