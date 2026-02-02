package pt.ipcb.car.pooling.notifications.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.notifications.modules.entities.NotificationEntity;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId);

    long countByRecipientUserIdAndReadFalse(UUID recipientUserId);
}
