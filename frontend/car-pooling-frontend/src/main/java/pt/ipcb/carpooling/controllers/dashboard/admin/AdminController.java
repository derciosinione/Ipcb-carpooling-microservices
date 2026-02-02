package pt.ipcb.carpooling.controllers.dashboard.admin;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.dto.UserDto;
import pt.ipcb.carpooling.dto.VehicleDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class AdminController {

    private static final BigDecimal EMISSIONS_PER_KM_KG = new BigDecimal("0.12");

    private final IdentityClient identityClient;
    private final TripsClient tripsClient;
    private final VehicleClient vehicleClient;

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        List<UserDto.UserResponse> users = identityClient.getAllUsersForAdmin();
        List<TripDto.TripResponse> trips = tripsClient.getAllTrips();
        List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();

        long totalUsers = users.size();
        long activeUsers = users.stream().filter(u -> Boolean.TRUE.equals(u.getActive())).count();
        long totalTrips = trips.size();

        BigDecimal co2SavedKg = trips.stream()
                .filter(t -> t.getDistanceKm() != null)
                .filter(t -> "FINISHED".equalsIgnoreCase(t.getStatus()))
                .map(t -> {
                    int travelers = t.getTotalTravelers() != null ? t.getTotalTravelers() : 1;
                    int extraPeople = Math.max(travelers - 1, 0);
                    return t.getDistanceKm()
                            .multiply(BigDecimal.valueOf(extraPeople))
                            .multiply(EMISSIONS_PER_KM_KG);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalTrips", totalTrips);
        model.addAttribute("co2SavedKg", co2SavedKg);
        model.addAttribute("users", users);
        model.addAttribute("vehicles", vehicles);
        return "dashboard/admin";
    }

    @PostMapping("/admin/users/status")
    public String updateUserStatus(@RequestParam String userId,
            @RequestParam boolean active,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        identityClient.updateUserStatus(userId, new UserDto.AdminStatusRequest(active));
        redirectAttributes.addFlashAttribute("success", "Estado do utilizador atualizado.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/admin/users/profiles/add")
    public String addUserProfile(@RequestParam String userId,
            @RequestParam String profileName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        identityClient.addProfileToUser(userId, profileName);
        redirectAttributes.addFlashAttribute("success", "Perfil adicionado com sucesso.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/admin/users/profiles/remove")
    public String removeUserProfile(@RequestParam String userId,
            @RequestParam String profileName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        identityClient.removeProfileFromUser(userId, profileName);
        redirectAttributes.addFlashAttribute("success", "Perfil removido com sucesso.");
        return "redirect:/dashboard/admin";
    }

    @PostMapping("/admin/users/create-admin")
    public String createAdmin(@RequestParam String name,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        AuthDto.RegisterRequest request = AuthDto.RegisterRequest.builder()
                .name(name)
                .username(username)
                .email(email)
                .password(password)
                .build();
        identityClient.createAdmin(request);
        redirectAttributes.addFlashAttribute("success", "Administrador criado com sucesso.");
        return "redirect:/dashboard/admin";
    }

    @GetMapping("/admin/reports/export")
    public ResponseEntity<byte[]> exportReports(@RequestParam String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null || !hasAdminRole(user)) {
            return ResponseEntity.status(403).build();
        }

        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        String csv;
        String filename;

        if ("users".equalsIgnoreCase(type)) {
            List<UserDto.UserResponse> users = identityClient.getAllUsersForAdmin();
            List<UserDto.UserResponse> filtered = users.stream()
                    .filter(u -> isWithinRange(u.getCreatedAt(), start, end))
                    .toList();
            csv = buildUsersCsv(filtered);
            filename = "relatorio-utilizadores.csv";
        } else {
            List<TripDto.TripResponse> trips = tripsClient.getAllTrips();
            List<TripDto.TripResponse> filtered = trips.stream()
                    .filter(t -> isWithinRange(t.getCreatedAt(), start, end))
                    .toList();
            csv = buildTripsCsv(filtered);
            filename = "relatorio-viagens.csv";
        }

        byte[] content = csv.getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .body(content);
    }

    @PostMapping("/admin/vehicles/delete")
    public String deleteVehicle(@RequestParam String vehicleId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (!hasAdminRole(user)) {
            return "redirect:/dashboard";
        }

        vehicleClient.deleteVehicle(vehicleId);
        redirectAttributes.addFlashAttribute("success", "Veículo removido com sucesso.");
        return "redirect:/dashboard/admin";
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private boolean isWithinRange(LocalDateTime createdAt, LocalDate start, LocalDate end) {
        if (createdAt == null) {
            return start == null && end == null;
        }
        LocalDate date = createdAt.toLocalDate();
        if (start != null && date.isBefore(start)) {
            return false;
        }
        if (end != null && date.isAfter(end)) {
            return false;
        }
        return true;
    }

    private String buildUsersCsv(List<UserDto.UserResponse> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,nome,email,ativo,perfis,criado_em").append('\n');
        for (UserDto.UserResponse user : users) {
            String profiles = user.getProfiles() != null
                    ? user.getProfiles().stream().map(UserDto.ProfileResponse::getName).collect(Collectors.joining("|"))
                    : "";
            sb.append(csv(user.getId())).append(',')
                    .append(csv(user.getName())).append(',')
                    .append(csv(user.getEmail())).append(',')
                    .append(csv(user.getActive() != null && user.getActive() ? "true" : "false")).append(',')
                    .append(csv(profiles)).append(',')
                    .append(csv(user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""))
                    .append('\n');
        }
        return sb.toString();
    }

    private String buildTripsCsv(List<TripDto.TripResponse> trips) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,origem,destino,data_partida,criado_em,estado,distancia_km,custo_total,total_pessoas,condutor_id,co2_poupado_kg")
                .append('\n');
        for (TripDto.TripResponse trip : trips) {
            int travelers = trip.getTotalTravelers() != null ? trip.getTotalTravelers() : 1;
            int extraPeople = Math.max(travelers - 1, 0);
            BigDecimal distance = trip.getDistanceKm() != null ? trip.getDistanceKm() : BigDecimal.ZERO;
            BigDecimal co2Saved = distance.multiply(BigDecimal.valueOf(extraPeople)).multiply(EMISSIONS_PER_KM_KG)
                    .setScale(2, RoundingMode.HALF_UP);

            sb.append(csv(trip.getId())).append(',')
                    .append(csv(trip.getOrigin())).append(',')
                    .append(csv(trip.getDestination())).append(',')
                    .append(csv(trip.getDepartureTime() != null ? trip.getDepartureTime().toString() : "")).append(',')
                    .append(csv(trip.getCreatedAt() != null ? trip.getCreatedAt().toString() : "")).append(',')
                    .append(csv(trip.getStatus())).append(',')
                    .append(csv(trip.getDistanceKm() != null ? trip.getDistanceKm().toString() : ""))
                    .append(',')
                    .append(csv(trip.getTotalCost() != null ? trip.getTotalCost().toString() : ""))
                    .append(',')
                    .append(csv(String.valueOf(travelers)))
                    .append(',')
                    .append(csv(trip.getDriverId()))
                    .append(',')
                    .append(csv(co2Saved.toString()))
                    .append('\n');
        }
        return sb.toString();
    }

    private String csv(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private boolean hasAdminRole(AuthDto.LoginResponse user) {
        return user.getRoles() != null
                && user.getRoles().stream().anyMatch(r -> Objects.equals(r, "Admin") || Objects.equals(r, "ADMIN"));
    }
}
