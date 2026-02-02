package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.BookingResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateBookingRequest;
import pt.ipcb.car.pooling.trips.modules.trip.service.BookingService;

import jakarta.servlet.http.HttpServletRequest;
import pt.ipcb.car.pooling.trips.exceptions.ForbiddenException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        BookingResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> acceptPost(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(bookingService.acceptBooking(id, userId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> rejectPost(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(bookingService.rejectBooking(id, userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelPost(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(bookingService.cancelBooking(id, userId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<BookingResponse> pay(@PathVariable UUID id, HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        return ResponseEntity.ok(bookingService.payBooking(id, userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<BookingResponse>> listByTrip(@PathVariable UUID tripId) {
        return ResponseEntity.ok(bookingService.listBookingsByTrip(tripId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<BookingResponse>> listByPassenger(@PathVariable UUID passengerId,
            HttpServletRequest httpRequest) {
        UUID userId = requireUserId(httpRequest);
        if (!passengerId.equals(userId)) {
            throw new ForbiddenException("Passenger ID does not match the authenticated user");
        }
        return ResponseEntity.ok(bookingService.listBookingsByPassenger(passengerId));
    }

    private UUID requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new ForbiddenException("Unauthorized");
        }
        return UUID.fromString(userId.toString());
    }
}
