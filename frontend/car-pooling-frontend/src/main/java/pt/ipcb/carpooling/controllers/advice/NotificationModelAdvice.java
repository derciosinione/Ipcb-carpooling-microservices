package pt.ipcb.carpooling.controllers.advice;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pt.ipcb.carpooling.clients.NotificationsClient;
import pt.ipcb.carpooling.dto.AuthDto;
import pt.ipcb.carpooling.dto.NotificationDto;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class NotificationModelAdvice {

    private final NotificationsClient notificationsClient;

    @ModelAttribute("headerNotifications")
    public List<NotificationDto.NotificationResponse> headerNotifications(HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return List.of();
        }
        try {
            List<NotificationDto.NotificationResponse> all = notificationsClient.myNotifications();
            return all.stream().limit(5).toList();
        } catch (Exception e) {
            log.debug("Could not load header notifications: {}", e.getMessage());
            return List.of();
        }
    }

    @ModelAttribute("headerUnreadCount")
    public long headerUnreadCount(HttpSession session) {
        AuthDto.LoginResponse user = (AuthDto.LoginResponse) session.getAttribute("user");
        if (user == null) {
            return 0L;
        }
        try {
            Map<String, Long> response = notificationsClient.unreadCount();
            return response.getOrDefault("count", 0L);
        } catch (Exception e) {
            log.debug("Could not load unread count: {}", e.getMessage());
            return 0L;
        }
    }
}
