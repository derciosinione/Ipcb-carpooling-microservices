package pt.ipcb.car.pooling.trips.modules.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;
import pt.ipcb.car.pooling.trips.modules.entities.BookingStatusEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingStatusRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.BookingResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateBookingRequest;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.BookingMapper;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request){
        TripEntity trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found with id: "));

        if (trip.getAvailableSeats() < request.getSeats()){
            throw new RuntimeException("Not enough seats available");
        }

        BookingStatusEntity pendingStatus = bookingStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new RuntimeException("Status 'PENDING' not found in"));

        BookingEntity booking = new BookingEntity();
        booking.setTrip(trip);
        booking.setPassengerId(request.getPassengerId());
        booking.setStatus(pendingStatus);
        booking.setPriceToPay(BigDecimal.ZERO);
        booking.setSeats(request.getSeats());

        BookingEntity savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse acceptBooking(UUID bookingId){
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " ));

        if (!booking.getStatus().getName().equals("PENDING")){
            throw new RuntimeException("Only PEDIND  bookings can be accepted");
        }

        TripEntity trip = booking.getTrip();

        if (trip.getAvailableSeats() < booking.getSeats()){
            throw new RuntimeException("Not enough seats available to accept this booking");
        }

        BookingStatusEntity confirmedStatus = bookingStatusRepository.findByName("CONFIRMED")
                .orElseThrow(() -> new RuntimeException("Status 'CONFIRMED' not found "));

        booking.setStatus(confirmedStatus);

        trip.setAvailableSeats(trip.getAvailableSeats() - booking.getSeats());

        tripRepository.save(trip);
        BookingEntity savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse rejectBooking(UUID bookingId){
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (!booking.getStatus().getName().equals("PENDING")){
            throw new RuntimeException("Only PEDIND  bookings can be rejected");
        }

        BookingStatusEntity rejectedStatus = bookingStatusRepository.findByName("REJECTED")
                .orElseThrow(() -> new RuntimeException("Status 'REJECTED' not found "));

        booking.setStatus(rejectedStatus);

        BookingEntity savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

}
