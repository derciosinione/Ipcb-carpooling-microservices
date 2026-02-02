package pt.ipcb.carpooling.controllers.auth;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class FeignAuthExceptionHandler {

    @ExceptionHandler({FeignException.Unauthorized.class, FeignException.Forbidden.class})
    public String handleAuthErrors(HttpServletRequest request, HttpSession session,
            RedirectAttributes redirectAttributes, FeignException ex) {
        if (session != null) {
            session.invalidate();
        }

        String message = "Sessão expirada. Por favor, faça login.";
        if (ex instanceof FeignException.Forbidden) {
            message = "Acesso negado. Faça login com uma conta autorizada.";
        }

        String redirectTarget = "/";
        if (request != null) {
            redirectTarget = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null && !query.isBlank()) {
                redirectTarget += "?" + query;
            }
        }

        redirectAttributes.addFlashAttribute("error", message);
        String encodedRedirect = URLEncoder.encode(redirectTarget, StandardCharsets.UTF_8);
        return "redirect:/auth?tab=login&redirect=" + encodedRedirect;
    }
}
