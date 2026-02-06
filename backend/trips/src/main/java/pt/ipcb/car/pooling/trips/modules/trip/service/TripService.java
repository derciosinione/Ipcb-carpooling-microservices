package pt.ipcb.car.pooling.trips.modules.trip.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.trips.clients.GpsClient;
import pt.ipcb.car.pooling.trips.exceptions.BadRequestException;
import pt.ipcb.car.pooling.trips.exceptions.NotFoundException;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripStatusEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripStatusRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.notifications.CreateNotificationRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.LocationSuggestionResponse;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.TripMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final TripStatusRepository tripStatusRepository;
    private final TripMapper tripMapper;
    private final BookingRepository bookingRepository;
    private final GpsClient gpsClient;
    private final NotificationsService notificationsService;

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        if (request.getDriverId() == null) {
            throw new BadRequestException("Driver ID is mandatory");
        }
        TripEntity trip = tripMapper.toEntity(request);
        enrichTripWithGpsData(trip, request);

        TripStatusEntity openStatus = tripStatusRepository.findByName("OPEN")
                .orElseThrow(() -> new NotFoundException("Status 'OPEN' not found in database"));

        trip.setStatus(openStatus);

        TripEntity savedTrip = tripRepository.save(trip);

        return toResponseWithCosts(savedTrip);
    }

    private void enrichTripWithGpsData(TripEntity trip, CreateTripRequest request) {
        LocationSuggestionResponse origin = reverseLocation(request.getOriginLat(), request.getOriginLon());
        LocationSuggestionResponse destination = reverseLocation(request.getDestinationLat(),
                request.getDestinationLon());
        if (origin == null || destination == null) {
            throw new BadRequestException("Could not validate origin/destination on map");
        }

        trip.setOrigin(origin.getDisplayName());
        trip.setDestination(destination.getDisplayName());

        DistanceResponse distance = calculateDistance(new DistanceRequest(
                request.getOriginLat(),
                request.getOriginLon(),
                request.getDestinationLat(),
                request.getDestinationLon()));

        if (distance != null && distance.getDistanceKm() != null) {
            trip.setDistanceKm(distance.getDistanceKm());
        }
    }

    public List<TripResponse> getAllTrips(){
        List<TripEntity> trips = tripRepository.findAll();

        return trips.stream()
                .map(this::toResponseWithCosts)
                .toList();
    }

    public List<TripResponse> getAvailableTrip(){
        return tripRepository
                .findAvailableTrips().stream()
                .map(this::toResponseWithCosts)
                .toList();
    }

    public List<TripResponse> getTripsDriver(UUID driverId){
        return tripRepository.findByDriverId(driverId).stream()
                .map(this::toResponseWithCosts)
                .toList();
    }

    public List<TripResponse> getTripByPassenger(UUID passengerId){
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(booking -> toResponseWithCosts(booking.getTrip()))
                .toList();
    }

    public List<TripResponse> searchTrips(String origin, String destination, Integer seats){
        List<TripEntity> trips = tripRepository.searchTrips(origin, destination, seats);
        return trips.stream()
                .map(this::toResponseWithCosts)
                .toList();
    }

    public List<TripResponse> nearbyTrips(Double userLat, Double userLon, BigDecimal radiusKm, Integer limit) {
        BigDecimal safeRadius = radiusKm == null || radiusKm.compareTo(BigDecimal.ZERO) <= 0
                ? BigDecimal.valueOf(25)
                : radiusKm;
        int safeLimit = limit == null || limit <= 0 ? 10 : Math.min(limit, 50);

        return tripRepository.findAvailableTrips().stream()
                .filter(t -> t.getOriginLat() != null && t.getOriginLon() != null)
                .map(t -> {
                    TripResponse response = toResponseWithCosts(t);
                    response.setDistanceFromUserKm(
                            haversineKm(userLat, userLon, t.getOriginLat(), t.getOriginLon()));
                    return response;
                })
                .filter(r -> r.getDistanceFromUserKm() != null
                        && r.getDistanceFromUserKm().compareTo(safeRadius) <= 0)
                .sorted(Comparator.comparing(TripResponse::getDistanceFromUserKm))
                .limit(safeLimit)
                .toList();
    }

    public TripResponse getTripById(UUID tripId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));
        return toResponseWithCosts(trip);
    }

    @Transactional
    public TripResponse updateTripStatus(UUID tripId, String newStatus) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));

        TripStatusEntity status = tripStatusRepository.findByName(newStatus)
                .orElseThrow(() -> new NotFoundException("Status not found: " + newStatus));
        String previousStatus = trip.getStatus().getName();

        if ("FINISHED".equals(previousStatus) || "CANCELED".equals(previousStatus)) {
            throw new BadRequestException("Trip cannot be updated after it is finished or canceled");
        }

        if (previousStatus.equals(newStatus)) {
            throw new BadRequestException("Trip is already in status " + newStatus);
        }

        trip.setStatus(status);
        TripEntity saved = tripRepository.save(trip);
        notifyTripLifecycleStatus(saved, newStatus);
        return toResponseWithCosts(saved);
    }

    private void notifyTripLifecycleStatus(TripEntity trip, String newStatus) {
        if (!"STARTED".equals(newStatus) && !"FINISHED".equals(newStatus)) {
            return;
        }

        List<BookingEntity> bookings = bookingRepository.findByTripId(trip.getId());
        Set<UUID> recipients = bookings.stream()
                .filter(booking -> booking.getPassengerId() != null)
                .filter(booking -> booking.getStatus() != null)
                .filter(booking -> "CONFIRMED".equals(booking.getStatus().getName()))
                .map(BookingEntity::getPassengerId)
                .collect(Collectors.toSet());

        if (recipients.isEmpty()) {
            return;
        }

        String title = "STARTED".equals(newStatus) ? "Viagem iniciada" : "Viagem terminada";
        String message = "STARTED".equals(newStatus)
                ? "A viagem " + trip.getOrigin() + " -> " + trip.getDestination() + " acabou de iniciar."
                : "A viagem " + trip.getOrigin() + " -> " + trip.getDestination() + " terminou.";
        String type = "STARTED".equals(newStatus) ? "TRIP_STARTED" : "TRIP_FINISHED";

        recipients.forEach(passengerId -> notifySafely(new CreateNotificationRequest(
                passengerId,
                title,
                message,
                type)));
    }

    private void notifySafely(CreateNotificationRequest request) {
        notificationsService.send(request);
    }

    @CircuitBreaker(name = "gpsReverse", fallbackMethod = "reverseFallback")
    private LocationSuggestionResponse reverseLocation(Double lat, Double lon) {
        return gpsClient.reverse(lat, lon);
    }

    private LocationSuggestionResponse reverseFallback(Double lat, Double lon, Throwable throwable) {
        log.warn("GPS reverse unavailable: {}", throwable.getMessage());
        return null;
    }

    @CircuitBreaker(name = "gpsDistance", fallbackMethod = "distanceFallback")
    private DistanceResponse calculateDistance(DistanceRequest request) {
        return gpsClient.distance(request);
    }

    private DistanceResponse distanceFallback(DistanceRequest request, Throwable throwable) {
        log.warn("GPS distance unavailable: {}", throwable.getMessage());
        return null;
    }

    private TripResponse toResponseWithCosts(TripEntity trip) {
        TripResponse response = tripMapper.toResponse(trip);

        Integer confirmedSeats = bookingRepository.sumConfirmedSeatsByTripId(trip.getId());
        int confirmed = confirmedSeats != null ? confirmedSeats : 0;
        int totalTravelers = confirmed + 1;

        BigDecimal totalCost = trip.getTotalCost() == null ? BigDecimal.ZERO : trip.getTotalCost();
        BigDecimal costPerSeat = BigDecimal.ZERO;
        costPerSeat = totalCost
                .divide(BigDecimal.valueOf(totalTravelers), 2, RoundingMode.HALF_UP);

        response.setConfirmedSeats(confirmed);
        response.setTotalTravelers(totalTravelers);
        response.setCostPerSeat(costPerSeat);
        return response;
    }

    private BigDecimal haversineKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371.0 * c;
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

}
