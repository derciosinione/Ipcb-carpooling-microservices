package pt.ipcb.carpooling.controllers.advice;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pt.ipcb.carpooling.dto.AuthDto;

import java.util.List;

@ControllerAdvice
public class UserRoleModelAdvice {

    @ModelAttribute("hasDriverRole")
    public boolean hasDriverRole(HttpSession session) {
        return hasRole(session, "Condutor", "DRIVER");
    }

    @ModelAttribute("hasPassengerRole")
    public boolean hasPassengerRole(HttpSession session) {
        return hasRole(session, "Passageiro", "PASSENGER");
    }

    @ModelAttribute("hasBothRoles")
    public boolean hasBothRoles(HttpSession session) {
        return hasDriverRole(session) && hasPassengerRole(session);
    }

    @ModelAttribute("hasAdminRole")
    public boolean hasAdminRole(HttpSession session) {
        return hasRole(session, "Admin", "ADMIN");
    }

    @ModelAttribute("defaultIsDriver")
    public boolean defaultIsDriver(HttpSession session) {
        boolean driver = hasDriverRole(session);
        boolean passenger = hasPassengerRole(session);
        return driver && !passenger || (driver && passenger);
    }

    private boolean hasRole(HttpSession session, String... candidates) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null || user.getRoles() == null) {
            return false;
        }
        List<String> roles = user.getRoles();
        for (String candidate : candidates) {
            for (String role : roles) {
                if (role != null && role.equalsIgnoreCase(candidate)) {
                    return true;
                }
            }
        }
        return false;
    }
}
