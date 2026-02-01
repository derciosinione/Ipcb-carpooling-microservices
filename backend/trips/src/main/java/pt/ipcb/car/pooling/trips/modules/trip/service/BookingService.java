package pt.ipcb.car.pooling.trips.modules.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.trips.exceptions.BadRequestException;
import pt.ipcb.car.pooling.trips.exceptions.ForbiddenException;
import pt.ipcb.car.pooling.trips.exceptions.NotFoundException;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;
import pt.ipcb.car.pooling.trips.modules.entities.BookingStatusEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingStatusRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripStatusRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.BookingResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateBookingRequest;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.BookingMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final TripStatusRepository tripStatusRepository;
    private final BookingMapper bookingMapper;
    private final TripCostService tripCostService;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, UUID userId){
        TripEntity trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + request.getTripId()));

        if (!"OPEN".equals(trip.getStatus().getName())) {
            throw new BadRequestException("Trip is not open for bookings");
        }

        if (trip.getAvailableSeats() < request.getSeats()){
            throw new BadRequestException("Not enough seats available");
        }

        BookingStatusEntity pendingStatus = bookingStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new NotFoundException("Status 'PENDING' not found"));

        if (request.getPassengerId() == null) {
            request.setPassengerId(userId);
        } else if (!request.getPassengerId().equals(userId)) {
            throw new ForbiddenException("Passenger ID does not match the authenticated user");
        }

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
    public BookingResponse acceptBooking(UUID bookingId, UUID userId){
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getTrip().getDriverId().equals(userId)) {
            throw new ForbiddenException("Only the driver can accept bookings");
        }

        if (!booking.getStatus().getName().equals("PENDING")){
            throw new BadRequestException("Only pending bookings can be accepted");
        }

        TripEntity trip = booking.getTrip();

        if (trip.getAvailableSeats() < booking.getSeats()){
            throw new BadRequestException("Not enough seats available to accept this booking");
        }

        BookingStatusEntity confirmedStatus = bookingStatusRepository.findByName("CONFIRMED")
                .orElseThrow(() -> new NotFoundException("Status 'CONFIRMED' not found"));

        booking.setStatus(confirmedStatus);

        trip.setAvailableSeats(trip.getAvailableSeats() - booking.getSeats());
        updateTripSeatStatus(trip);

        tripRepository.save(trip);
        BookingEntity savedBooking = bookingRepository.save(booking);
        tripCostService.recalculateCosts(trip.getId());

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse rejectBooking(UUID bookingId, UUID userId){
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getTrip().getDriverId().equals(userId)) {
            throw new ForbiddenException("Only the driver can reject bookings");
        }

        if (!booking.getStatus().getName().equals("PENDING")){
            throw new BadRequestException("Only pending bookings can be rejected");
        }

        BookingStatusEntity rejectedStatus = bookingStatusRepository.findByName("REJECTED")
                .orElseThrow(() -> new NotFoundException("Status 'REJECTED' not found"));

        booking.setStatus(rejectedStatus);

        BookingEntity savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(UUID bookingId, UUID userId){
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        boolean isDriver = booking.getTrip().getDriverId().equals(userId);
        boolean isPassenger = booking.getPassengerId().equals(userId);
        if (!isDriver && !isPassenger) {
            throw new ForbiddenException("Only the driver or the passenger can cancel this booking");
        }

        if ("CANCELED".equals(booking.getStatus().getName())) {
            throw new BadRequestException("Booking is already canceled");
        }

        BookingStatusEntity canceledStatus = bookingStatusRepository.findByName("CANCELED")
                .orElseThrow(() -> new NotFoundException("Status 'CANCELED' not found"));

        TripEntity trip = booking.getTrip();
        if ("CONFIRMED".equals(booking.getStatus().getName())) {
            trip.setAvailableSeats(trip.getAvailableSeats() + booking.getSeats());
            updateTripSeatStatus(trip);
            tripRepository.save(trip);
        }

        booking.setStatus(canceledStatus);
        BookingEntity saved = bookingRepository.save(booking);
        tripCostService.recalculateCosts(trip.getId());

        return bookingMapper.toResponse(saved);
    }

    public List<BookingResponse> listBookingsByTrip(UUID tripId) {
        return bookingRepository.findByTripId(tripId).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    public List<BookingResponse> listBookingsByPassenger(UUID passengerId) {
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    private void updateTripSeatStatus(TripEntity trip) {
        if (trip.getAvailableSeats() <= 0 && !"FULL".equals(trip.getStatus().getName())) {
            trip.setAvailableSeats(0);
            tripStatusRepository.findByName("FULL")
                    .ifPresent(trip::setStatus);
        } else if (trip.getAvailableSeats() > 0 && "FULL".equals(trip.getStatus().getName())) {
            tripStatusRepository.findByName("OPEN")
                    .ifPresent(trip::setStatus);
        }
    }
}
