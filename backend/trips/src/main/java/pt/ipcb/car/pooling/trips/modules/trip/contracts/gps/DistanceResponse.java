package pt.ipcb.car.pooling.trips.modules.trip.contracts.gps;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DistanceResponse {
    private BigDecimal distanceKm;
}
