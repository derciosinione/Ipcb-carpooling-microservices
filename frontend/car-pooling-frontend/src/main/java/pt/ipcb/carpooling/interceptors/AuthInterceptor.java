package pt.ipcb.carpooling.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("token") == null) {
            String errorMessage = URLEncoder.encode("Sessão expirada ou acesso restrito. Por favor, faça login.",
                    StandardCharsets.UTF_8);
            response.sendRedirect("/auth?error=" + errorMessage);
            return false;
        }
        return true;
    }
}
