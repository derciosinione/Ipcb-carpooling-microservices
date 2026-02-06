package pt.ipcb.car.pooling.notifications.modules.notifications.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateNotificationRequest {

    @NotNull
    private UUID recipientUserId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotBlank
    private String type;
}
