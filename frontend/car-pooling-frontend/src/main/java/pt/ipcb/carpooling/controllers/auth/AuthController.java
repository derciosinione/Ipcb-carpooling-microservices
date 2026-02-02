package pt.ipcb.carpooling.controllers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.RegisterForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final IdentityClient identityClient;

    @Value("${api.gateway.url}")
    private String gatewayUrl;

    @GetMapping("/auth")
    public String auth(@RequestParam(name = "tab", defaultValue = "login") String tab,
            @RequestParam(name = "redirect", required = false) String redirect,
            Model model,
            HttpSession session) {

        // If already logged in, redirect to dashboard
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }

        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }

        model.addAttribute("tab", tab);
        if (redirect != null && !redirect.isBlank()) {
            model.addAttribute("redirect", redirect);
        }
        model.addAttribute("gatewayUrl", gatewayUrl);
        return "auth";
    }

    @PostMapping("/auth/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            @RequestParam(name = "redirect", required = false) String redirect,
            HttpSession session) {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest(email, password);
        AuthDto.LoginResponse response = identityClient.signIn(request);

        // Store user data in session
        session.setAttribute("token", response.getToken());
        session.setAttribute("user", response);

        // Calculate initials
        String initials = "";
        if (response.getName() != null && !response.getName().isBlank()) {
            String[] parts = response.getName().trim().split("\\s+");
            if (parts.length > 0 && !parts[0].isEmpty()) {
                initials += parts[0].charAt(0);
            }
            if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
                initials += parts[parts.length - 1].charAt(0);
            }
        } else {
            initials = "U";
        }
        session.setAttribute("userInitials", initials.toUpperCase());
        session.setAttribute("userName", response.getName());

        String safeRedirect = sanitizeRedirect(redirect);
        return "redirect:" + (safeRedirect != null ? safeRedirect : "/dashboard");
    }

    @PostMapping("/auth/register")
    public String register(RegisterForm form,
            RedirectAttributes redirectAttributes) {

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "As palavras-passe não coincidem.");
            redirectAttributes.addFlashAttribute("registerForm", form);
            return "redirect:/auth?tab=register";
        }

        if (!form.isPassenger() && !form.isDriver()) {
            redirectAttributes.addFlashAttribute("error", "Selecione pelo menos um perfil.");
            redirectAttributes.addFlashAttribute("registerForm", form);
            return "redirect:/auth?tab=register";
        }

        AuthDto.RegisterRequest request = AuthDto.RegisterRequest.builder()
                .username(form.getUsername())
                .email(form.getEmail())
                .password(form.getPassword())
                .name((form.getFirstName() + " " + form.getLastName()).trim())
                .build();

        if (form.isPassenger() && form.isDriver()) {
            identityClient.registerBoth(request);
        } else if (form.isDriver()) {
            identityClient.registerDriver(request);
        } else {
            identityClient.registerPassenger(request);
        }

        redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso! Por favor, inicie sessão.");
        return "redirect:/auth?tab=login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth";
    }

    private String sanitizeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) {
            return null;
        }
        if (!redirect.startsWith("/") || redirect.startsWith("//")) {
            return null;
        }
        if (redirect.startsWith("/auth") || redirect.startsWith("/logout")) {
            return null;
        }
        return redirect;
    }
}
