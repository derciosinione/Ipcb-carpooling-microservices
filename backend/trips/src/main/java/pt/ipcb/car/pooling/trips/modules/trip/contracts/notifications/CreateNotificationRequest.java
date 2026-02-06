package pt.ipcb.car.pooling.trips.modules.trip.contracts.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateNotificationRequest {
    private UUID recipientUserId;
    private String title;
    private String message;
    private String type;
}
