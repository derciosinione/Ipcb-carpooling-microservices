package pt.ipcb.carpooling.controllers.dashboard.vehicles;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
public class VehiclesController {
    private final VehicleClient vehicleClient;

    @GetMapping("/vehicles")
    public String vehicles(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");

        List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getVehiclesByOwner(user.getId());
        model.addAttribute("vehicles", vehicles);

        List<VehicleDto.BrandResponse> brands = vehicleClient.getAllBrands();
        model.addAttribute("brands", brands);

        return "dashboard/vehicles";
    }

    @PostMapping("/vehicles")
    public String createVehicle(VehicleDto.VehicleRequest request, HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");

        request.setOwnerId(user.getId());
        if (request.getSeats() == null) {
            request.setSeats(4);
        }

        vehicleClient.createVehicle(request);
        redirectAttributes.addFlashAttribute("success", "Veículo adicionado com sucesso!");

        return "redirect:/dashboard/vehicles";
    }
}
