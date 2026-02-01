package pt.ipcb.car.pooling.trips.modules.trip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.trips.modules.repositories.BookingRepository;
import pt.ipcb.car.pooling.trips.modules.repositories.TripRepository;
import pt.ipcb.car.pooling.trips.modules.trip.contracts.UserMetricsResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public UserMetricsResponse getDriverMetrics(UUID driverId) {
        long totalTrips = tripRepository.countByDriverId(driverId);
        BigDecimal totalEarnings = tripRepository.sumTotalCostByDriverId(driverId);
        BigDecimal totalKm = tripRepository.sumDistanceByDriverId(driverId);

        return UserMetricsResponse.builder()
                .totalTrips(totalTrips)
                .totalEarnings(totalEarnings)
                .totalKm(totalKm)
                .build();
    }

    public UserMetricsResponse getPassengerMetrics(UUID passengerId) {
        long totalTrips = bookingRepository.countConfirmedByPassengerId(passengerId);
        BigDecimal totalSpend = bookingRepository.sumPriceToPayByPassengerId(passengerId);
        BigDecimal totalKm = bookingRepository.sumDistanceByPassengerId(passengerId);

        return UserMetricsResponse.builder()
                .totalTrips(totalTrips)
                .totalSpend(totalSpend)
                .totalKm(totalKm)
                .build();
    }
}
