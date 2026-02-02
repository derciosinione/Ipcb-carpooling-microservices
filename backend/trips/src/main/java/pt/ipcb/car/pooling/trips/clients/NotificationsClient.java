package pt.ipcb.car.pooling.trips.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.notifications.CreateNotificationRequest;

@FeignClient(name = "car-pooling-notifications-api", contextId = "notificationsClient")
public interface NotificationsClient {

    @PostMapping("/api/v1/notifications")
    void create(@RequestBody CreateNotificationRequest request);
}
