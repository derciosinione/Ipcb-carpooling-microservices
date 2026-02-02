package pt.ipcb.carpooling.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.GpsClient;
import pt.ipcb.carpooling.clients.IdentityClient;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.clients.VehicleClient;
import pt.ipcb.carpooling.dto.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final VehicleClient vehicleClient;
    private final TripsClient tripsClient;
    private final IdentityClient identityClient;
    private final GpsClient gpsClient;

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
        if (trips == null)
            return Collections.emptyList();
        return trips.stream()
                .filter(trip -> !"FINISHED".equalsIgnoreCase(trip.getStatus())
                        && !"CANCELED".equalsIgnoreCase(trip.getStatus()))
                .collect(Collectors.toList());
    }

    public List<TripDto.TripResponse> filterHistory(List<TripDto.TripResponse> trips) {
        if (trips == null)
            return Collections.emptyList();
        return trips.stream()
                .filter(trip -> "FINISHED".equalsIgnoreCase(trip.getStatus())
                        || "CANCELED".equalsIgnoreCase(trip.getStatus()))
                .collect(Collectors.toList());
    }

    public Map<String, UserDto.UserResponse> fetchUsersByIds(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return Map.of();
        UserDto.BatchUsersRequest request = new UserDto.BatchUsersRequest(ids);
        try {
            return identityClient.getUsersByIds(request).stream()
                    .collect(Collectors.toMap(UserDto.UserResponse::getId, u -> u));
        } catch (Exception e) {
            log.error("Error fetching users by ids: {}", e.getMessage());
            return Map.of();
        }
    }

    public void saveUserLocation(String userId, String label, Double lat, Double lon) {
        if (userId == null || label == null || label.isBlank() || lat == null || lon == null)
            return;
        try {
            LocationDto.SaveUserLocationRequest request = new LocationDto.SaveUserLocationRequest();
            request.setLabel(label);
            request.setLat(lat);
            request.setLon(lon);
            gpsClient.saveRecentLocation(userId, request);
        } catch (Exception e) {
            log.warn("Could not save recent location for user {}: {}", userId, e.getMessage());
        }
    }

    public VehicleDto.VehicleResponse findVehicleById(String id) {
        if (id == null)
            return null;
        try {
            List<VehicleDto.VehicleResponse> vehicles = vehicleClient.getAllVehicles();
            return vehicles.stream().filter(v -> Objects.equals(v.getId(), id)).findFirst().orElse(null);
        } catch (Exception e) {
            log.error("Error loading vehicles: {}", e.getMessage());
            return null;
        }
    }

    public String safeName(UserDto.UserResponse user) {
        if (user == null)
            return "Utilizador";
        if (user.getName() != null && !user.getName().isBlank())
            return user.getName();
        if (user.getEmail() != null && !user.getEmail().isBlank())
            return user.getEmail();
        return "Utilizador";
    }

    public String initials(UserDto.UserResponse user) {
        String name = safeName(user);
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0)
            return "U";
        String first = parts[0];
        String last = parts.length > 1 ? parts[parts.length - 1] : "";
        String init = "";
        if (!first.isEmpty())
            init += first.charAt(0);
        if (!last.isEmpty())
            init += last.charAt(0);
        return init.isEmpty() ? "U" : init.toUpperCase();
    }

    // Payment helpers and CSV builders copied from controller
    public List<DriverPaymentDto> buildDriverPayments(String driverId) {
        List<TripDto.TripResponse> driverTrips = tripsClient.getTripsByDriver(driverId);
        if (driverTrips == null || driverTrips.isEmpty())
            return List.of();

        List<DriverPaymentDto> result = new ArrayList<>();
        for (TripDto.TripResponse trip : driverTrips) {
            List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByTrip(trip.getId());
            for (BookingDto.BookingResponse booking : bookings) {
                if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus()))
                    continue;
                result.add(new DriverPaymentDto(
                        trip.getId(),
                        trip.getOrigin() + " -> " + trip.getDestination(),
                        trip.getDepartureTime(),
                        booking.getPassengerId(),
                        booking.getSeats(),
                        booking.getPriceToPay(),
                        Boolean.TRUE.equals(booking.getPaid()),
                        booking.getPaymentReference()));
            }
        }

        Set<String> passengerIds = result.stream()
                .map(DriverPaymentDto::getPassengerName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<String, UserDto.UserResponse> users = fetchUsersByIds(new ArrayList<>(passengerIds));
        result.forEach(payment -> {
            UserDto.UserResponse u = users.get(payment.getPassengerName());
            payment.setPassengerName(safeName(u));
        });

        return result.stream().sorted((a, b) -> b.getDepartureTime().compareTo(a.getDepartureTime())).toList();
    }

    public List<PassengerPaymentDto> buildPassengerPayments(String passengerId) {
        List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByPassenger(passengerId);
        if (bookings == null || bookings.isEmpty())
            return List.of();

        Map<String, TripDto.TripResponse> tripsById = new HashMap<>();
        List<PassengerPaymentDto> result = new ArrayList<>();
        for (BookingDto.BookingResponse booking : bookings) {
            if (booking.getTripId() == null)
                continue;
            TripDto.TripResponse trip = tripsById.get(booking.getTripId());
            if (trip == null) {
                trip = tripsClient.getTripById(booking.getTripId());
                tripsById.put(booking.getTripId(), trip);
            }
            result.add(new PassengerPaymentDto(
                    booking.getTripId(),
                    trip.getOrigin() + " -> " + trip.getDestination(),
                    trip.getDepartureTime(),
                    booking.getSeats(),
                    booking.getPriceToPay(),
                    Boolean.TRUE.equals(booking.getPaid()),
                    booking.getPaymentReference(),
                    booking.getStatus()));
        }
        return result.stream().sorted((a, b) -> b.getDepartureTime().compareTo(a.getDepartureTime())).toList();
    }

    // Passenger trips helpers
    public List<PassengerTripDto> buildPassengerTrips(List<BookingDto.BookingResponse> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return List.of();
        }

        Map<String, TripDto.TripResponse> tripsById = new HashMap<>();
        List<PassengerTripDto> result = new ArrayList<>();
        for (BookingDto.BookingResponse booking : bookings) {
            if (booking == null || booking.getTripId() == null)
                continue;
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
        if (trips == null)
            return Collections.emptyList();
        return trips.stream()
                .filter(trip -> trip.getTrip() != null)
                .filter(trip -> !"FINISHED".equalsIgnoreCase(trip.getTrip().getStatus())
                        && !"CANCELED".equalsIgnoreCase(trip.getTrip().getStatus()))
                .collect(Collectors.toList());
    }

    public List<PassengerTripDto> filterHistoryPassengerTrips(List<PassengerTripDto> trips) {
        if (trips == null)
            return Collections.emptyList();
        return trips.stream()
                .filter(trip -> trip.getTrip() != null)
                .filter(trip -> "FINISHED".equalsIgnoreCase(trip.getTrip().getStatus())
                        || "CANCELED".equalsIgnoreCase(trip.getTrip().getStatus()))
                .collect(Collectors.toList());
    }

    public java.math.BigDecimal sumDriverCollected(List<DriverPaymentDto> items) {
        return items.stream().filter(i -> Boolean.TRUE.equals(i.getPaid())).map(DriverPaymentDto::getAmount)
                .filter(Objects::nonNull).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumDriverPending(List<DriverPaymentDto> items) {
        return items.stream().filter(i -> !Boolean.TRUE.equals(i.getPaid())).map(DriverPaymentDto::getAmount)
                .filter(Objects::nonNull).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumPassengerPaid(List<PassengerPaymentDto> items) {
        return items.stream().filter(i -> Boolean.TRUE.equals(i.getPaid())).map(PassengerPaymentDto::getAmount)
                .filter(Objects::nonNull).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumPassengerPending(List<PassengerPaymentDto> items) {
        return items.stream().filter(i -> "CONFIRMED".equalsIgnoreCase(i.getBookingStatus()))
                .filter(i -> !Boolean.TRUE.equals(i.getPaid())).map(PassengerPaymentDto::getAmount)
                .filter(Objects::nonNull).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public String normalizePeriod(String period) {
        if ("today".equalsIgnoreCase(period))
            return "today";
        if ("year".equalsIgnoreCase(period))
            return "year";
        if ("all".equalsIgnoreCase(period))
            return "all";
        return "month";
    }

    public List<DriverPaymentDto> filterDriverPaymentsByPeriod(List<DriverPaymentDto> items, String period) {
        String normalized = normalizePeriod(period);
        if ("all".equals(normalized))
            return items;
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        int thisYear = today.getYear();
        return items.stream().filter(i -> i.getDepartureTime() != null).filter(i -> {
            LocalDate date = i.getDepartureTime().toLocalDate();
            if ("today".equals(normalized))
                return date.equals(today);
            if ("month".equals(normalized))
                return YearMonth.from(date).equals(thisMonth);
            return date.getYear() == thisYear;
        }).toList();
    }

    public List<PassengerPaymentDto> filterPassengerPaymentsByPeriod(List<PassengerPaymentDto> items, String period) {
        String normalized = normalizePeriod(period);
        if ("all".equals(normalized))
            return items;
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        int thisYear = today.getYear();
        return items.stream().filter(i -> i.getDepartureTime() != null).filter(i -> {
            LocalDate date = i.getDepartureTime().toLocalDate();
            if ("today".equals(normalized))
                return date.equals(today);
            if ("month".equals(normalized))
                return YearMonth.from(date).equals(thisMonth);
            return date.getYear() == thisYear;
        }).toList();
    }

    public String buildDriverCsv(List<DriverPaymentDto> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("trip_id,rota,data_hora,passageiro,lugares,valor,pago,referencia\n");
        for (DriverPaymentDto i : items) {
            sb.append(csv(i.getTripId())).append(",")
                    .append(csv(i.getTripRoute())).append(",")
                    .append(csv(i.getDepartureTime() != null ? i.getDepartureTime().toString() : "")).append(",")
                    .append(csv(i.getPassengerName())).append(",")
                    .append(csv(i.getSeats())).append(",")
                    .append(csv(i.getAmount())).append(",")
                    .append(csv(Boolean.TRUE.equals(i.getPaid()) ? "SIM" : "NAO")).append(",")
                    .append(csv(i.getPaymentReference())).append("\n");
        }
        return sb.toString();
    }

    public String buildPassengerCsv(List<PassengerPaymentDto> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("trip_id,rota,data_hora,lugares,valor,pago,referencia,estado_reserva\n");
        for (PassengerPaymentDto i : items) {
            sb.append(csv(i.getTripId())).append(",")
                    .append(csv(i.getTripRoute())).append(",")
                    .append(csv(i.getDepartureTime() != null ? i.getDepartureTime().toString() : "")).append(",")
                    .append(csv(i.getSeats())).append(",")
                    .append(csv(i.getAmount())).append(",")
                    .append(csv(Boolean.TRUE.equals(i.getPaid()) ? "SIM" : "NAO")).append(",")
                    .append(csv(i.getPaymentReference())).append(",")
                    .append(csv(i.getBookingStatus())).append("\n");
        }
        return sb.toString();
    }

    private String csv(Object value) {
        String raw = value == null ? "" : String.valueOf(value);
        String escaped = raw.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    public java.math.BigDecimal sumTotalCost(List<TripDto.TripResponse> trips) {
        if (trips == null)
            return java.math.BigDecimal.ZERO;
        return trips.stream().map(TripDto.TripResponse::getTotalCost).filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumCostPerSeat(List<TripDto.TripResponse> trips) {
        if (trips == null)
            return java.math.BigDecimal.ZERO;
        return trips.stream().map(TripDto.TripResponse::getCostPerSeat).filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
