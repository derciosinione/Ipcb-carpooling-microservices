package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingResponse {
    private UUID id;
    private UUID tripId;
    private UUID passengerId;
    private String status;
    private LocalDateTime createdAt;
}
