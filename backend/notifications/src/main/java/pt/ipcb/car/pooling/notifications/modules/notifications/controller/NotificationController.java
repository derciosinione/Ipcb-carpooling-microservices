package pt.ipcb.car.pooling.notifications.modules.notifications.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.notifications.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.notifications.modules.notifications.contracts.CreateNotificationRequest;
import pt.ipcb.car.pooling.notifications.modules.notifications.contracts.NotificationResponse;
import pt.ipcb.car.pooling.notifications.modules.notifications.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> myNotifications(HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(notificationService.listByUser(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(Map.of("count", notificationService.unreadCount(userId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(notificationService.markRead(id, userId));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        notificationService.markAllRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        notificationService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ForbiddenException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }
}
