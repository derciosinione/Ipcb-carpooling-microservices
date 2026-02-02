package pt.ipcb.carpooling.controllers.dashboard.search;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pt.ipcb.carpooling.clients.GpsClient;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.LocationDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.services.identity.IdentityDashboardService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller("dashboardSearchController")
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SearchController {
    private final TripsClient tripsClient;
    private final GpsClient gpsClient;
    private final IdentityDashboardService identityDashboardService;

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "1") Integer seats,
            @RequestParam(required = false) Double nearbyLat,
            @RequestParam(required = false) Double nearbyLon,
            @RequestParam(required = false, defaultValue = "25") BigDecimal nearbyRadiusKm,
            Model model,
            HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("currentUserId", user.getId());
        }
        if (origin != null && destination != null) {
            List<TripDto.TripResponse> results = tripsClient.searchTrips(origin, destination, seats);
            model.addAttribute("results", results);
            Map<String, pt.ipcb.carpooling.dto.UserDto.UserResponse> users = identityDashboardService
                    .fetchUsersByIds(results.stream()
                            .map(TripDto.TripResponse::getDriverId)
                            .filter(Objects::nonNull)
                            .toList());
            model.addAttribute("userNames", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.safeName(e.getValue()))));
            model.addAttribute("userInitials", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.initials(e.getValue()))));
        } else {
            model.addAttribute("results", List.of());
        }

        if (nearbyLat != null && nearbyLon != null) {
            List<TripDto.TripResponse> nearbyResults = tripsClient.nearbyTrips(nearbyLat, nearbyLon, nearbyRadiusKm,
                    10);
            model.addAttribute("nearbyResults", nearbyResults);
            Map<String, pt.ipcb.carpooling.dto.UserDto.UserResponse> users = identityDashboardService
                    .fetchUsersByIds(nearbyResults.stream()
                            .map(TripDto.TripResponse::getDriverId)
                            .filter(Objects::nonNull)
                            .toList());
            model.addAttribute("nearbyUserNames", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.safeName(e.getValue()))));
            model.addAttribute("nearbyUserInitials", users.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> identityDashboardService.initials(e.getValue()))));
        } else {
            model.addAttribute("nearbyResults", List.of());
        }
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);
        model.addAttribute("seats", seats);
        model.addAttribute("nearbyLat", nearbyLat);
        model.addAttribute("nearbyLon", nearbyLon);
        model.addAttribute("nearbyRadiusKm", nearbyRadiusKm);
        return "dashboard/search";
    }

    @GetMapping("/locations/search")
    @ResponseBody
    public List<LocationDto.LocationSuggestionResponse> searchLocations(@RequestParam("q") String query) {
        if (query == null || query.isBlank() || query.trim().length() < 2) {
            return List.of();
        }
        return gpsClient.searchLocations(query.trim(), 6);
    }
}
