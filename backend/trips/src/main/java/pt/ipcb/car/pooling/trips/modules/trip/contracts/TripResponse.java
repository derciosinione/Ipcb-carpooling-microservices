package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripResponse {
    private UUID id;
    private UUID driverId;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private Integer availableSeats;
    private BigDecimal totalCost;
    private String status; // Aqui devolvemos apenas o nome (Ex: "OPEN")
    private LocalDateTime createdAt;
}
