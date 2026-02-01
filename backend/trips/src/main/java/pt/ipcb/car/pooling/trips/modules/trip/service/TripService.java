package pt.ipcb.car.pooling.trips.modules.trip.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripStatusEntity;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripStatusRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.CreateTripRequest;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.TripResponse;
import pt.ipcb.car.pooling.trips.modules.trip.mapper.TripMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripStatusRepository tripStatusRepository;
    private final TripMapper tripMapper;
    private final BookingRepository bookingRepository;

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        TripEntity trip = tripMapper.toEntity(request);

        TripStatusEntity openStatus = tripStatusRepository.findByName("OPEN")
                .orElseThrow(() -> new RuntimeException("Status 'OPEN' not found in database"));

        trip.setStatus(openStatus);

        TripEntity savedTrip = tripRepository.save(trip);

        return tripMapper.toResponse(savedTrip);
    }

    public List<TripResponse> getAllTrips(){
        List<TripEntity> trips = tripRepository.findAll();

        return trips.stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    public List<TripResponse> getAvailableTrip(){
        return tripRepository
                .findAvailableTrips().stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    public List<TripResponse> getTripsDriver(UUID driverId){
        return tripRepository.findByDriverId(driverId).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    public List<TripResponse> getTripByPassenger(UUID passengerId){
        return bookingRepository.findByPassengerId(passengerId).stream()
                .map(booking -> tripMapper.toResponse(booking.getTrip()))
                .toList();
    }

    public List<TripResponse> searchTrips(String origin, String destination, Integer seats){
        List<TripEntity> trips = tripRepository.searchTrips(origin, destination, seats);
        return trips.stream()
                .map(tripMapper::toResponse)
                .toList();
    }


}
