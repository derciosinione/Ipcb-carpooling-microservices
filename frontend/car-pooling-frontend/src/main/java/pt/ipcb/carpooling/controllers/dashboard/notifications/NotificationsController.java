package pt.ipcb.carpooling.controllers.dashboard.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ipcb.carpooling.clients.NotificationsClient;
import pt.ipcb.carpooling.dto.NotificationDto;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class NotificationsController {
    private final NotificationsClient notificationsClient;

    @GetMapping("/notifications")
    public String notifications(@RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model) {
        String normalizedFilter = "unread".equalsIgnoreCase(filter) ? "unread" : "all";
        List<NotificationDto.NotificationResponse> notifications = notificationsClient.myNotifications();
        if ("unread".equals(normalizedFilter)) {
            notifications = notifications.stream()
                    .filter(n -> !Boolean.TRUE.equals(n.getRead()))
                    .toList();
        }
        model.addAttribute("notifications", notifications);
        model.addAttribute("filter", normalizedFilter);
        return "dashboard/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead() {
        notificationsClient.markAllRead();
        return "redirect:/dashboard/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable String id) {
        notificationsClient.markRead(id);
        return "redirect:/dashboard/notifications";
    }

    @PostMapping("/notifications/{id}/delete")
    public String deleteNotification(@PathVariable String id) {
        notificationsClient.delete(id);
        return "redirect:/dashboard/notifications";
    }
}
