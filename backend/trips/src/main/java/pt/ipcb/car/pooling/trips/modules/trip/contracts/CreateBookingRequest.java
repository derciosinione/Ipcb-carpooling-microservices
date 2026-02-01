package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateBookingRequest {
    @NotNull(message = "Trip ID is mandatory")
    private UUID tripId;

    private UUID passengerId;

    @NotNull(message = "Number of Seat is mandatory")
    @Min(value = 1, message = "At least 1 seat must be booked")
    private Integer seats;
}
