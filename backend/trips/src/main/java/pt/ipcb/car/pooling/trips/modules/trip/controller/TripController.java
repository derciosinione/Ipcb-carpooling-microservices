package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.trips.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.UpdateTripStatusRequest;
import pt.ipcb.car.pooling.trips.modules.trip.service.TripService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody CreateTripRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (request.getDriverId() == null) {
            request.setDriverId(userId);
        } else if (!request.getDriverId().equals(userId)) {
            throw new ForbiddenException("Driver ID does not match the authenticated user");
        }
        TripResponse response = tripService.createTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAll() {
        List<TripResponse> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripResponse>> listAvailable() {
        return ResponseEntity.ok(tripService.getAvailableTrip());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TripResponse>> listByDriver(@PathVariable UUID driverId,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (!driverId.equals(userId)) {
            throw new ForbiddenException("Driver ID does not match the authenticated user");
        }
        return ResponseEntity.ok(tripService.getTripsDriver(driverId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<TripResponse>> listByPassenger(@PathVariable UUID passengerId,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (!passengerId.equals(userId)) {
            throw new ForbiddenException("Passenger ID does not match the authenticated user");
        }
        return ResponseEntity.ok(tripService.getTripByPassenger(passengerId));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getById(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripService.getTripById(tripId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(defaultValue = "1") Integer seats) {
        return ResponseEntity.ok(tripService.searchTrips(origin, destination, seats));
    }

    @PatchMapping("/{tripId}/status")
    public ResponseEntity<TripResponse> updateStatus(@PathVariable UUID tripId,
            @Valid @RequestBody UpdateTripStatusRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        TripResponse trip = tripService.getTripById(tripId);
        if (!trip.getDriverId().equals(userId)) {
            throw new ForbiddenException("Only the driver can update trip status");
        }
        return ResponseEntity.ok(tripService.updateTripStatus(tripId, request.getStatus().toUpperCase()));
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ForbiddenException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }
}
