package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTripRequest {
    @NotNull(message = "Driver ID is mandatory")
    private UUID driverId;

    @NotNull(message = "Vehicle ID is mandatory")
    private UUID vehicleId;

    @NotBlank(message = "Origin cannot be empty")
    private String origin;

    @NotBlank(message = "Destination cannot be empty")
    private String destination;

    @NotNull(message = "Departure time is mandatory")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;

    @Min(value = 1, message = "At least 1 seat must be available")
    private Integer availableSeats;
}
