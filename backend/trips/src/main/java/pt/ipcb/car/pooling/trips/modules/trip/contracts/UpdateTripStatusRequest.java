package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTripStatusRequest {
    @NotBlank(message = "Status is mandatory")
    private String status;
}
