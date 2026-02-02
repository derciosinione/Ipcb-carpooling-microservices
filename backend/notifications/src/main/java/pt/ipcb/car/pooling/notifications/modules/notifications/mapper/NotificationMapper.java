package pt.ipcb.car.pooling.notifications.modules.notifications.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.notifications.modules.entities.NotificationEntity;
import pt.ipcb.car.pooling.notifications.modules.notifications.contracts.NotificationResponse;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(NotificationEntity entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setRecipientUserId(entity.getRecipientUserId());
        response.setTitle(entity.getTitle());
        response.setMessage(entity.getMessage());
        response.setType(entity.getType());
        response.setRead(Boolean.TRUE.equals(entity.getRead()));
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}
