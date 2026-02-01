package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;
import pt.ipcb.car.pooling.trips.modules.trip.service.TripService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> create(@Valid @RequestBody CreateTripRequest request) {
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
    public ResponseEntity<List<TripResponse>> listByDriver(@PathVariable UUID driverId) {
        return ResponseEntity.ok(tripService.getTripsDriver(driverId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<TripResponse>> listByPassenger(@PathVariable UUID passengerId) {
        return ResponseEntity.ok(tripService.getTripByPassenger(passengerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(defaultValue = "1") Integer seats) {
        return ResponseEntity.ok(tripService.searchTrips(origin, destination, seats));
    }

}
