package pt.ipcb.car.pooling.gps.modules.location.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ipcb.car.pooling.gps.modules.location.contracts.DistanceRequest;
import pt.ipcb.car.pooling.gps.modules.location.contracts.DistanceResponse;
import pt.ipcb.car.pooling.gps.modules.location.contracts.LocationSuggestionResponse;
import pt.ipcb.car.pooling.gps.modules.location.contracts.SaveUserLocationRequest;
import pt.ipcb.car.pooling.gps.modules.location.service.LocationService;
import pt.ipcb.car.pooling.gps.modules.location.service.UserLocationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final UserLocationService userLocationService;

    @GetMapping("/search")
    public ResponseEntity<List<LocationSuggestionResponse>> search(
            @RequestParam("q") String query,
            @RequestParam(name = "limit", defaultValue = "8") Integer limit) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        int safeLimit = Math.max(1, Math.min(limit, 10));
        return ResponseEntity.ok(locationService.search(query.trim(), safeLimit));
    }

    @GetMapping("/reverse")
    public ResponseEntity<LocationSuggestionResponse> reverse(
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon) {
        LocationSuggestionResponse response = locationService.reverse(lat, lon);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/distance")
    public ResponseEntity<DistanceResponse> distance(@Valid @RequestBody DistanceRequest request) {
        return ResponseEntity.ok(new DistanceResponse(locationService.calculateDistanceKm(
                request.getOriginLat(),
                request.getOriginLon(),
                request.getDestinationLat(),
                request.getDestinationLon())));
    }

    @PostMapping("/users/{userId}/recent")
    public ResponseEntity<LocationSuggestionResponse> saveRecent(@PathVariable UUID userId,
            @Valid @RequestBody SaveUserLocationRequest request) {
        return ResponseEntity.ok(userLocationService.saveRecent(userId, request));
    }

    @GetMapping("/users/{userId}/recent")
    public ResponseEntity<List<LocationSuggestionResponse>> listRecent(@PathVariable UUID userId) {
        return ResponseEntity.ok(userLocationService.listRecent(userId));
    }
}
