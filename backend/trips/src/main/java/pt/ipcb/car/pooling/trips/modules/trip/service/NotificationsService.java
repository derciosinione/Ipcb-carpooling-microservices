package pt.ipcb.car.pooling.trips.modules.trip.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.trips.clients.NotificationsClient;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.notifications.CreateNotificationRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsService {

    private final NotificationsClient notificationsClient;

    @CircuitBreaker(name = "notifications", fallbackMethod = "notifyFallback")
    public void send(CreateNotificationRequest request) {
        notificationsClient.create(request);
    }

    private void notifyFallback(CreateNotificationRequest request, Throwable throwable) {
        log.warn("Notification service unavailable: {}", throwable.getMessage());
    }
}
