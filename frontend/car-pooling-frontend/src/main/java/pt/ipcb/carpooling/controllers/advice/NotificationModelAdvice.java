package pt.ipcb.carpooling.controllers.advice;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pt.ipcb.carpooling.clients.NotificationsClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.NotificationDto;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class NotificationModelAdvice {

    private final NotificationsClient notificationsClient;

    @ModelAttribute("headerNotifications")
    public List<NotificationDto.NotificationResponse> headerNotifications(HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }
        List<NotificationDto.NotificationResponse> all = notificationsClient.myNotifications();
        return all.stream().limit(5).toList();
    }

    @ModelAttribute("headerUnreadCount")
    public long headerUnreadCount(HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return 0L;
        }
        Map<String, Long> response = notificationsClient.unreadCount();
        return response.getOrDefault("count", 0L);
    }
}
