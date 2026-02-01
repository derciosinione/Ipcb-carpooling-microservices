package pt.ipcb.car.pooling.trips.modules.trip.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.trips.clients.GpsClient;
import pt.ipcb.car.pooling.trips.exceptions.BadRequestException;
import pt.ipcb.car.pooling.trips.exceptions.NotFoundException;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripStatusEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripStatusRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.DistanceResponse;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.gps.LocationSuggestionResponse;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.TripMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final TripRepository tripRepository;
    private final TripStatusRepository tripStatusRepository;
    private final TripMapper tripMapper;
    private final BookingRepository bookingRepository;
    private final GpsClient gpsClient;

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
        try {
            LocationSuggestionResponse origin = gpsClient.reverse(request.getOriginLat(), request.getOriginLon());
            LocationSuggestionResponse destination = gpsClient.reverse(request.getDestinationLat(),
                    request.getDestinationLon());
            if (origin == null || destination == null) {
                throw new BadRequestException("Origin or destination coordinates are invalid");
            }

            trip.setOrigin(origin.getDisplayName());
            trip.setDestination(destination.getDisplayName());

            DistanceResponse distance = gpsClient.distance(new DistanceRequest(
                    request.getOriginLat(),
                    request.getOriginLon(),
                    request.getDestinationLat(),
                    request.getDestinationLon()));

            if (distance != null && distance.getDistanceKm() != null) {
                trip.setDistanceKm(distance.getDistanceKm());
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to enrich trip with GPS data: {}", e.getMessage());
            throw new BadRequestException("Could not validate origin/destination on map");
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

        if ("FINISHED".equals(trip.getStatus().getName()) || "CANCELED".equals(trip.getStatus().getName())) {
            throw new BadRequestException("Trip cannot be updated after it is finished or canceled");
        }

        trip.setStatus(status);
        TripEntity saved = tripRepository.save(trip);
        return toResponseWithCosts(saved);
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

}
