package pt.ipcb.car.pooling.gps.modules.location.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistanceResponse {
    private BigDecimal distanceKm;
}
