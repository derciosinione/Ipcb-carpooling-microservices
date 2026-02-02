package pt.ipcb.carpooling.controllers.dashboard.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipcb.carpooling.clients.NotificationsClient;
import pt.ipcb.carpooling.dto.NotificationDto;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class NotificationsController {
    private final NotificationsClient notificationsClient;

    @GetMapping("/notifications")
    public String notifications(@RequestParam(name = "filter", defaultValue = "all") String filter,
            Model model) {
        try {
            String normalizedFilter = "unread".equalsIgnoreCase(filter) ? "unread" : "all";
            List<NotificationDto.NotificationResponse> notifications = notificationsClient.myNotifications();
            if ("unread".equals(normalizedFilter)) {
                notifications = notifications.stream()
                        .filter(n -> !Boolean.TRUE.equals(n.getRead()))
                        .toList();
            }
            model.addAttribute("notifications", notifications);
            model.addAttribute("filter", normalizedFilter);
        } catch (Exception e) {
            log.error("Error loading notifications: {}", e.getMessage());
            model.addAttribute("notifications", List.of());
            model.addAttribute("filter", "all");
            model.addAttribute("error", "Erro ao carregar notificações.");
        }
        return "dashboard/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(RedirectAttributes redirectAttributes) {
        try {
            notificationsClient.markAllRead();
        } catch (Exception e) {
            log.error("Error marking notifications read: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Não foi possível marcar todas as notificações como lidas.");
        }
        return "redirect:/dashboard/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            notificationsClient.markRead(id);
        } catch (Exception e) {
            log.error("Error marking notification {} read: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Não foi possível marcar a notificação como lida.");
        }
        return "redirect:/dashboard/notifications";
    }

    @PostMapping("/notifications/{id}/delete")
    public String deleteNotification(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            notificationsClient.delete(id);
        } catch (Exception e) {
            log.error("Error deleting notification {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Não foi possível eliminar a notificação.");
        }
        return "redirect:/dashboard/notifications";
    }
}