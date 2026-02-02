package pt.ipcb.carpooling.controllers.advice;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.dto.RegisterForm;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public Object handleFeignException(FeignException ex,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        log.error("Feign error on {} {}: {}", safeMethod(request), safePath(request), ex.getMessage());

        if (isLocationsSearch(request)) {
            return ResponseEntity.ok(List.of());
        }

        if (isAuthLogin(request)) {
            redirectAttributes.addFlashAttribute("error", "Erro ao iniciar sessão. Verifique os seus dados.");
            String safeRedirect = sanitizeRedirect(request != null ? request.getParameter("redirect") : null);
            return "redirect:" + buildAuthRedirect("login", safeRedirect);
        }

        if (isAuthRegister(request)) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar conta. Tente outro email/username.");
            redirectAttributes.addFlashAttribute("registerForm", buildRegisterForm(request));
            return "redirect:" + buildAuthRedirect("register", null);
        }

        if (!isGet(request)) {
            redirectAttributes.addFlashAttribute("error", "Não foi possível concluir a operação.");
            return "redirect:" + fallbackRedirect(request);
        }

        setStatus(response, ex.status());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        log.error("Unhandled error on {} {}: {}", safeMethod(request), safePath(request), ex.getMessage());

        if (isLocationsSearch(request)) {
            return ResponseEntity.ok(List.of());
        }

        if (!isGet(request)) {
            redirectAttributes.addFlashAttribute("error", "Ocorreu um erro inesperado.");
            return "redirect:" + fallbackRedirect(request);
        }

        setStatus(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "error";
    }

    private boolean isAuthLogin(HttpServletRequest request) {
        return request != null && "/auth/login".equals(request.getRequestURI());
    }

    private boolean isAuthRegister(HttpServletRequest request) {
        return request != null && "/auth/register".equals(request.getRequestURI());
    }

    private boolean isLocationsSearch(HttpServletRequest request) {
        return request != null && "/dashboard/locations/search".equals(request.getRequestURI());
    }

    private boolean isGet(HttpServletRequest request) {
        return request == null || "GET".equalsIgnoreCase(request.getMethod());
    }

    private String buildAuthRedirect(String tab, String redirect) {
        String base = "/auth?tab=" + tab;
        if (redirect == null || redirect.isBlank()) {
            return base;
        }
        return base + "&redirect=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8);
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

    private String fallbackRedirect(HttpServletRequest request) {
        if (request == null) {
            return "/dashboard";
        }
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/dashboard";
        }
        return referer;
    }

    private RegisterForm buildRegisterForm(HttpServletRequest request) {
        RegisterForm form = new RegisterForm();
        if (request == null) {
            return form;
        }
        form.setFirstName(request.getParameter("firstName"));
        form.setLastName(request.getParameter("lastName"));
        form.setUsername(request.getParameter("username"));
        form.setEmail(request.getParameter("email"));
        form.setPassword(request.getParameter("password"));
        form.setConfirmPassword(request.getParameter("confirmPassword"));
        form.setPassenger("on".equalsIgnoreCase(request.getParameter("passenger")));
        form.setDriver("on".equalsIgnoreCase(request.getParameter("driver")));
        return form;
    }

    private void setStatus(HttpServletResponse response, int status) {
        if (response == null) {
            return;
        }
        response.setStatus(status > 0 ? status : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private String safeMethod(HttpServletRequest request) {
        return request != null ? request.getMethod() : "UNKNOWN";
    }

    private String safePath(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "unknown";
    }
}
