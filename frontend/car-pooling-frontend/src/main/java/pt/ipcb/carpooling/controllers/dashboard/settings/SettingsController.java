package pt.ipcb.carpooling.controllers.dashboard.settings;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SettingsController {

    private final IdentityClient identityClient;

    @GetMapping("/settings")
    public String settings(Model model, HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        model.addAttribute("roles", user.getRoles() != null ? user.getRoles() : List.of());
        var profile = identityClient.getUserById(user.getId());
        model.addAttribute("profileName", profile.getName());
        model.addAttribute("profilePhone", profile.getPhone());
        model.addAttribute("profileDescription", profile.getDescription());
        return "dashboard/settings";
    }

    @PostMapping("/settings/roles/add")
    public String addRole(@RequestParam("role") String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        String profileName = normalizeProfile(role);
        if (profileName == null) {
            redirectAttributes.addFlashAttribute("error", "Perfil inválido.");
            return "redirect:/dashboard/settings";
        }

        List<String> roles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();
        boolean alreadyHas = roles.stream().anyMatch(r -> r != null && r.equalsIgnoreCase(profileName));
        if (alreadyHas) {
            redirectAttributes.addFlashAttribute("error", "Já possui este perfil.");
            return "redirect:/dashboard/settings";
        }

        identityClient.addProfileToUser(user.getId(), profileName);
        roles.add(profileName);
        user.setRoles(roles);
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("success", "Perfil adicionado com sucesso.");

        return "redirect:/dashboard/settings";
    }

    @PostMapping("/settings/roles/remove")
    public String removeRole(@RequestParam("role") String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        String profileName = normalizeProfile(role);
        if (profileName == null) {
            redirectAttributes.addFlashAttribute("error", "Perfil inválido.");
            return "redirect:/dashboard/settings";
        }

        List<String> roles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();
        boolean hasRole = roles.stream().anyMatch(r -> r != null && r.equalsIgnoreCase(profileName));
        if (!hasRole) {
            redirectAttributes.addFlashAttribute("error", "Não possui este perfil.");
            return "redirect:/dashboard/settings";
        }
        if (roles.size() <= 1) {
            redirectAttributes.addFlashAttribute("error", "Deve manter pelo menos um perfil.");
            return "redirect:/dashboard/settings";
        }

        identityClient.removeProfileFromUser(user.getId(), profileName);
        roles.removeIf(r -> r != null && r.equalsIgnoreCase(profileName));
        user.setRoles(roles);
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("success", "Perfil removido com sucesso.");

        return "redirect:/dashboard/settings";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        UserDto.UpdateUserRequest request = new UserDto.UpdateUserRequest(name, phone, description);
        UserDto.UserResponse response = identityClient.updateUser(user.getId(), request);
        user.setName(response.getName());
        session.setAttribute("user", user);
        session.setAttribute("userName", response.getName());
        session.setAttribute("userInitials", buildInitials(response.getName()));
        redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso.");

        return "redirect:/dashboard/settings";
    }

    private String normalizeProfile(String role) {
        if (role == null) {
            return null;
        }
        if ("driver".equalsIgnoreCase(role) || "condutor".equalsIgnoreCase(role)) {
            return "Condutor";
        }
        if ("passenger".equalsIgnoreCase(role) || "passageiro".equalsIgnoreCase(role)) {
            return "Passageiro";
        }
        return null;
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) {
            return "U";
        }
        String[] parts = name.trim().split("\\s+");
        String initials = "";
        if (parts.length > 0 && !parts[0].isEmpty()) {
            initials += parts[0].charAt(0);
        }
        if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
            initials += parts[parts.length - 1].charAt(0);
        }
        return initials.toUpperCase();
    }
}
