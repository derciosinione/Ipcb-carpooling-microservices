package pt.ipcb.carpooling.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import pt.ipcb.carpooling.dto.NotificationDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "cloud-gateway", url = "${api.gateway.url}", contextId = "notificationsClient")
public interface NotificationsClient {

    @GetMapping("/notifications/api/v1/notifications")
    List<NotificationDto.NotificationResponse> myNotifications();

    @GetMapping("/notifications/api/v1/notifications/unread-count")
    Map<String, Long> unreadCount();

    @PutMapping("/notifications/api/v1/notifications/{id}/read")
    NotificationDto.NotificationResponse markRead(@PathVariable("id") String id);

    @PutMapping("/notifications/api/v1/notifications/read-all")
    void markAllRead();

    @DeleteMapping("/notifications/api/v1/notifications/{id}")
    void delete(@PathVariable("id") String id);
}
