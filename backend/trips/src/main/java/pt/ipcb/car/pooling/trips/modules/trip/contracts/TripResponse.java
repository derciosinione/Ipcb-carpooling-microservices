package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripResponse {
    private UUID id;
    private UUID driverId;
    private UUID vehicleId;
    private String origin;
    private String destination;
    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
    private String description;
    private LocalDateTime departureTime;
    private Integer availableSeats;
    private Integer confirmedSeats;
    private Integer totalTravelers;
    private BigDecimal totalCost;
    private BigDecimal costPerSeat;
    private BigDecimal distanceKm;
    private BigDecimal distanceFromUserKm;
    private String status; // Aqui devolvemos apenas o nome (Ex: "OPEN")
    private LocalDateTime createdAt;
}
