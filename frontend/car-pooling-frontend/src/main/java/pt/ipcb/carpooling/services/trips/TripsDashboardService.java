package pt.ipcb.carpooling.services.trips;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.MetricsDto;
import pt.ipcb.carpooling.dto.PassengerTripDto;
import pt.ipcb.carpooling.dto.TripDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripsDashboardService {

    private final TripsClient tripsClient;

    public List<TripDto.TripResponse> safeGetTripsByDriver(String userId) {
        try {
            return tripsClient.getTripsByDriver(userId);
        } catch (Exception e) {
            log.error("Error loading driver trips for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public List<TripDto.TripResponse> safeGetTripsByPassenger(String userId) {
        try {
            return tripsClient.getTripsByPassenger(userId);
        } catch (Exception e) {
            log.error("Error loading passenger trips for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public MetricsDto.MetricsResponse safeGetMetrics(String role) {
        try {
            return tripsClient.getMetrics(role);
        } catch (Exception e) {
            log.error("Error loading metrics for {}: {}", role, e.getMessage());
            return null;
        }
    }

    public List<TripDto.TripResponse> filterUpcoming(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> !"FINISHED".equalsIgnoreCase(trip.getStatus())
                        && !"CANCELED".equalsIgnoreCase(trip.getStatus()))
                .toList();
    }

    public List<TripDto.TripResponse> filterHistory(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> "FINISHED".equalsIgnoreCase(trip.getStatus())
                        || "CANCELED".equalsIgnoreCase(trip.getStatus()))
                .toList();
    }

    public List<PassengerTripDto> buildPassengerTrips(List<BookingDto.BookingResponse> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return List.of();
        }

        Map<String, TripDto.TripResponse> tripsById = new HashMap<>();
        List<PassengerTripDto> result = new java.util.ArrayList<>();
        for (BookingDto.BookingResponse booking : bookings) {
            if (booking == null || booking.getTripId() == null) {
                continue;
            }
            TripDto.TripResponse trip = tripsById.get(booking.getTripId());
            if (trip == null) {
                try {
                    trip = tripsClient.getTripById(booking.getTripId());
                    tripsById.put(booking.getTripId(), trip);
                } catch (Exception e) {
                    log.error("Error loading trip {} for booking {}: {}", booking.getTripId(), booking.getId(),
                            e.getMessage());
                    continue;
                }
            }
            result.add(new PassengerTripDto(trip, booking));
        }
        return result;
    }

    public List<PassengerTripDto> filterUpcomingPassengerTrips(List<PassengerTripDto> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> trip.getTrip() != null)
                .filter(trip -> !"FINISHED".equalsIgnoreCase(trip.getTrip().getStatus())
                        && !"CANCELED".equalsIgnoreCase(trip.getTrip().getStatus()))
                .toList();
    }

    public List<PassengerTripDto> filterHistoryPassengerTrips(List<PassengerTripDto> trips) {
        if (trips == null) {
            return Collections.emptyList();
        }
        return trips.stream()
                .filter(trip -> trip.getTrip() != null)
                .filter(trip -> "FINISHED".equalsIgnoreCase(trip.getTrip().getStatus())
                        || "CANCELED".equalsIgnoreCase(trip.getTrip().getStatus()))
                .toList();
    }

    public java.math.BigDecimal sumTotalCost(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return java.math.BigDecimal.ZERO;
        }
        return trips.stream().map(TripDto.TripResponse::getTotalCost)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumCostPerSeat(List<TripDto.TripResponse> trips) {
        if (trips == null) {
            return java.math.BigDecimal.ZERO;
        }
        return trips.stream().map(TripDto.TripResponse::getCostPerSeat)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
