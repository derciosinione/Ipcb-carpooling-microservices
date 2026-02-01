package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.trips.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.UserMetricsResponse;
import pt.ipcb.car.pooling.trips.modules.trip.service.MetricsService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping
    public ResponseEntity<UserMetricsResponse> getMetrics(@RequestParam String role,
            HttpServletRequest request) {
        UUID userId = requireUserId(request);
        if ("DRIVER".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(metricsService.getDriverMetrics(userId));
        }
        if ("PASSENGER".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(metricsService.getPassengerMetrics(userId));
        }
        return ResponseEntity.badRequest().build();
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ForbiddenException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }
}
