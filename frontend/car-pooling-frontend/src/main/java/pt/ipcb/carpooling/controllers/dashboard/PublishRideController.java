package pt.ipcb.carpooling.controllers.dashboard;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.PublishRideForm;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.DashboardService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class PublishRideController {
    private final VehicleClient vehicleClient;
    private final TripsClient tripsClient;
    private final DashboardService dashboardService;

    @GetMapping("/publish-ride")
    public String publishRide(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        try {
            List<?> vehicles = vehicleClient.getVehiclesByOwner(user.getId());
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
                    .originLat(form.getOriginLat())
                    .originLon(form.getOriginLon())
                    .destinationLat(form.getDestinationLat())
                    .destinationLon(form.getDestinationLon())
                    .description(form.getDescription())
                    .departureTime(departureTime)
                    .availableSeats(form.getSeats())
                    .build();

            tripsClient.createTrip(request);
            dashboardService.saveUserLocation(user.getId(), form.getOrigin(), form.getOriginLat(), form.getOriginLon());
            dashboardService.saveUserLocation(user.getId(), form.getDestination(), form.getDestinationLat(),
                    form.getDestinationLon());
            redirectAttributes.addFlashAttribute("success", "Boleia publicada com sucesso!");
            return "redirect:/dashboard/rides";
        } catch (Exception e) {
            log.error("Error creating trip: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Erro ao publicar boleia. Verifique os dados.");
            return "redirect:/dashboard/publish-ride";
        }
    }
}