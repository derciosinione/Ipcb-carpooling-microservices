package pt.ipcb.carpooling.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("token") == null) {
            response.sendRedirect("/auth?error=Sessão expirada ou acesso restrito. Por favor, faça login.");
            return false;
        }
        return true;
    }
}
