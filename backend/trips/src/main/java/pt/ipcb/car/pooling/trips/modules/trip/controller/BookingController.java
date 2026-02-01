package pt.ipcb.car.pooling.trips.modules.trip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.BookingResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateBookingRequest;
import pt.ipcb.car.pooling.trips.modules.trip.service.BookingService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<BookingResponse> accept(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.acceptBooking(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<BookingResponse> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.rejectBooking(id));
    }
}
