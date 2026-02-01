package pt.ipcb.car.pooling.gps.modules.location.contracts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DistanceRequest {
    @NotNull
    private Double originLat;

    @NotNull
    private Double originLon;

    @NotNull
    private Double destinationLat;

    @NotNull
    private Double destinationLon;
}
