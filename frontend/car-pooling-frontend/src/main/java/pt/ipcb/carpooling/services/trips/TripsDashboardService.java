package pt.ipcb.carpooling.services.trips;

import lombok.RequiredArgsConstructor;
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
public class TripsDashboardService {

    private final TripsClient tripsClient;

    public List<TripDto.TripResponse> getTripsByDriver(String userId) {
        return tripsClient.getTripsByDriver(userId);
    }

    public List<TripDto.TripResponse> getTripsByPassenger(String userId) {
        return tripsClient.getTripsByPassenger(userId);
    }

    public MetricsDto.MetricsResponse getMetrics(String role) {
        return tripsClient.getMetrics(role);
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
                trip = tripsClient.getTripById(booking.getTripId());
                tripsById.put(booking.getTripId(), trip);
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
