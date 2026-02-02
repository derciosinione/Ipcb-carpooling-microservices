package pt.ipcb.carpooling.controllers.dashboard.vehicles;

import jakarta.servlet.http.HttpSession;
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

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class VehiclesController {
    private final VehicleClient vehicleClient;

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
}