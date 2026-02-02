package pt.ipcb.car.pooling.trips.modules.trip.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipcb.car.pooling.trips.clients.NotificationsClient;
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
import pt.ipcb.car.pooling.trips.modules.trip.contracts.notifications.CreateNotificationRequest;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.BookingMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final BookingStatusRepository bookingStatusRepository;
    private final TripStatusRepository tripStatusRepository;
    private final BookingMapper bookingMapper;
    private final TripCostService tripCostService;
    private final NotificationsClient notificationsClient;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, UUID userId){
        TripEntity trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + request.getTripId()));

        if (trip.getDriverId().equals(userId)) {
            throw new ForbiddenException("Driver cannot book their own trip");
        }

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
        booking.setPaid(false);
        booking.setPaidAt(null);
        booking.setPaymentReference(null);

        BookingEntity savedBooking = bookingRepository.save(booking);
        notifySafely(new CreateNotificationRequest(
                trip.getDriverId(),
                "Novo pedido de reserva",
                "Recebeu um pedido de reserva para a viagem " + trip.getOrigin() + " -> " + trip.getDestination()
                        + ".",
                "BOOKING_PENDING"));
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
        notifySafely(new CreateNotificationRequest(
                booking.getPassengerId(),
                "Reserva confirmada",
                "A sua reserva para a viagem " + trip.getOrigin() + " -> " + trip.getDestination()
                        + " foi confirmada.",
                "BOOKING_CONFIRMED"));

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
        notifySafely(new CreateNotificationRequest(
                booking.getPassengerId(),
                "Reserva rejeitada",
                "A sua reserva para a viagem " + booking.getTrip().getOrigin() + " -> "
                        + booking.getTrip().getDestination() + " foi rejeitada.",
                "BOOKING_REJECTED"));

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
        if (Boolean.TRUE.equals(booking.getPaid())) {
            throw new BadRequestException("Paid bookings cannot be canceled");
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
        UUID recipient = isDriver ? booking.getPassengerId() : booking.getTrip().getDriverId();
        notifySafely(new CreateNotificationRequest(
                recipient,
                "Reserva cancelada",
                "Uma reserva na viagem " + booking.getTrip().getOrigin() + " -> " + booking.getTrip().getDestination()
                        + " foi cancelada.",
                "BOOKING_CANCELED"));

        return bookingMapper.toResponse(saved);
    }

    @Transactional
    public BookingResponse payBooking(UUID bookingId, UUID userId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getPassengerId().equals(userId)) {
            throw new ForbiddenException("Only the passenger can pay for this booking");
        }

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus().getName())) {
            throw new BadRequestException("Only confirmed bookings can be paid");
        }

        if (Boolean.TRUE.equals(booking.getPaid())) {
            throw new BadRequestException("Booking is already paid");
        }

        if (booking.getPriceToPay() == null || booking.getPriceToPay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("There is no amount to pay yet");
        }

        booking.setPaid(true);
        booking.setPaidAt(LocalDateTime.now());
        booking.setPaymentReference("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        BookingEntity saved = bookingRepository.save(booking);
        notifySafely(new CreateNotificationRequest(
                booking.getTrip().getDriverId(),
                "Pagamento recebido",
                "O passageiro efetuou o pagamento da viagem " + booking.getTrip().getOrigin() + " -> "
                        + booking.getTrip().getDestination() + ".",
                "PAYMENT_DONE"));
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

    private void notifySafely(CreateNotificationRequest request) {
        sendNotification(request);
    }

    @CircuitBreaker(name = "notifications", fallbackMethod = "notifyFallback")
    private void sendNotification(CreateNotificationRequest request) {
        notificationsClient.create(request);
    }

    private void notifyFallback(CreateNotificationRequest request, Throwable throwable) {
        log.warn("Notification service unavailable: {}", throwable.getMessage());
    }
}
