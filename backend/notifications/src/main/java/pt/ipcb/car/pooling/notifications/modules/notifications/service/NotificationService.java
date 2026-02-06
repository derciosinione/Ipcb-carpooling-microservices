package pt.ipcb.car.pooling.notifications.modules.notifications.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.notifications.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.notifications.exceptions.NotFoundException;
import pt.ipcb.car.pooling.notifications.modules.entities.NotificationEntity;
import pt.ipcb.car.pooling.notifications.modules.notifications.contracts.CreateNotificationRequest;
import pt.ipcb.car.pooling.notifications.modules.notifications.contracts.NotificationResponse;
import pt.ipcb.car.pooling.notifications.modules.notifications.mapper.NotificationMapper;
import pt.ipcb.car.pooling.notifications.modules.repositories.NotificationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationResponse create(CreateNotificationRequest request) {
        NotificationEntity entity = new NotificationEntity();
        entity.setRecipientUserId(request.getRecipientUserId());
        entity.setTitle(request.getTitle());
        entity.setMessage(request.getMessage());
        entity.setType(request.getType());
        entity.setRead(false);
        return notificationMapper.toResponse(notificationRepository.save(entity));
    }

    public List<NotificationResponse> listByUser(UUID userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public long unreadCount(UUID userId) {
        return notificationRepository.countByRecipientUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(UUID id, UUID userId) {
        NotificationEntity entity = findOwned(id, userId);
        entity.setRead(true);
        return notificationMapper.toResponse(notificationRepository.save(entity));
    }

    @Transactional
    public void markAllRead(UUID userId) {
        List<NotificationEntity> items = notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
        items.forEach(i -> i.setRead(true));
        notificationRepository.saveAll(items);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        NotificationEntity entity = findOwned(id, userId);
        notificationRepository.delete(entity);
    }

    private NotificationEntity findOwned(UUID id, UUID userId) {
        NotificationEntity entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!entity.getRecipientUserId().equals(userId)) {
            throw new ForbiddenException("Notification does not belong to authenticated user");
        }
        return entity;
    }
}
