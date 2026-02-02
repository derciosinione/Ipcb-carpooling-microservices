package pt.ipcb.carpooling.services.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.carpooling.clients.TripsClient;
import pt.ipcb.carpooling.dto.BookingDto;
import pt.ipcb.carpooling.dto.DriverPaymentDto;
import pt.ipcb.carpooling.dto.PassengerPaymentDto;
import pt.ipcb.carpooling.dto.TripDto;
import pt.ipcb.carpooling.dto.UserDto;
import pt.ipcb.carpooling.services.identity.IdentityDashboardService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentsDashboardService {

    private final TripsClient tripsClient;
    private final IdentityDashboardService identityDashboardService;

    public List<DriverPaymentDto> buildDriverPayments(String driverId) {
        List<TripDto.TripResponse> driverTrips = tripsClient.getTripsByDriver(driverId);
        if (driverTrips == null || driverTrips.isEmpty()) {
            return List.of();
        }

        List<DriverPaymentDto> result = new ArrayList<>();
        for (TripDto.TripResponse trip : driverTrips) {
            List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByTrip(trip.getId());
            for (BookingDto.BookingResponse booking : bookings) {
                if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                    continue;
                }
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
        Map<String, UserDto.UserResponse> users = identityDashboardService
                .fetchUsersByIds(new ArrayList<>(passengerIds));
        result.forEach(payment -> {
            UserDto.UserResponse u = users.get(payment.getPassengerName());
            payment.setPassengerName(identityDashboardService.safeName(u));
        });

        return result.stream().sorted((a, b) -> b.getDepartureTime().compareTo(a.getDepartureTime())).toList();
    }

    public List<PassengerPaymentDto> buildPassengerPayments(String passengerId) {
        List<BookingDto.BookingResponse> bookings = tripsClient.getBookingsByPassenger(passengerId);
        if (bookings == null || bookings.isEmpty()) {
            return List.of();
        }

        Map<String, TripDto.TripResponse> tripsById = new HashMap<>();
        List<PassengerPaymentDto> result = new ArrayList<>();
        for (BookingDto.BookingResponse booking : bookings) {
            if (booking.getTripId() == null) {
                continue;
            }
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

    public java.math.BigDecimal sumDriverCollected(List<DriverPaymentDto> items) {
        return items.stream().filter(i -> Boolean.TRUE.equals(i.getPaid()))
                .map(DriverPaymentDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumDriverPending(List<DriverPaymentDto> items) {
        return items.stream().filter(i -> !Boolean.TRUE.equals(i.getPaid()))
                .map(DriverPaymentDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumPassengerPaid(List<PassengerPaymentDto> items) {
        return items.stream().filter(i -> Boolean.TRUE.equals(i.getPaid()))
                .map(PassengerPaymentDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public java.math.BigDecimal sumPassengerPending(List<PassengerPaymentDto> items) {
        return items.stream()
                .filter(i -> "CONFIRMED".equalsIgnoreCase(i.getBookingStatus()))
                .filter(i -> !Boolean.TRUE.equals(i.getPaid()))
                .map(PassengerPaymentDto::getAmount)
                .filter(Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public String normalizePeriod(String period) {
        if ("today".equalsIgnoreCase(period)) {
            return "today";
        }
        if ("year".equalsIgnoreCase(period)) {
            return "year";
        }
        if ("all".equalsIgnoreCase(period)) {
            return "all";
        }
        return "month";
    }

    public List<DriverPaymentDto> filterDriverPaymentsByPeriod(List<DriverPaymentDto> items, String period) {
        String normalized = normalizePeriod(period);
        if ("all".equals(normalized)) {
            return items;
        }
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        int thisYear = today.getYear();
        return items.stream().filter(i -> i.getDepartureTime() != null).filter(i -> {
            LocalDate date = i.getDepartureTime().toLocalDate();
            if ("today".equals(normalized)) {
                return date.equals(today);
            }
            if ("month".equals(normalized)) {
                return YearMonth.from(date).equals(thisMonth);
            }
            return date.getYear() == thisYear;
        }).toList();
    }

    public List<PassengerPaymentDto> filterPassengerPaymentsByPeriod(List<PassengerPaymentDto> items, String period) {
        String normalized = normalizePeriod(period);
        if ("all".equals(normalized)) {
            return items;
        }
        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        int thisYear = today.getYear();
        return items.stream().filter(i -> i.getDepartureTime() != null).filter(i -> {
            LocalDate date = i.getDepartureTime().toLocalDate();
            if ("today".equals(normalized)) {
                return date.equals(today);
            }
            if ("month".equals(normalized)) {
                return YearMonth.from(date).equals(thisMonth);
            }
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
}
