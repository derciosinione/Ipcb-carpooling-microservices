package pt.ipcb.car.pooling.trips.modules.trip.contracts.gps;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistanceRequest {
    private Double originLat;
    private Double originLon;
    private Double destinationLat;
    private Double destinationLon;
}
