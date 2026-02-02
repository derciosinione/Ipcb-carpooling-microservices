package pt.ipcb.car.pooling.notifications.modules.notifications.contracts;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private UUID recipientUserId;
    private String title;
    private String message;
    private String type;
    private Boolean read;
    private LocalDateTime createdAt;
}
