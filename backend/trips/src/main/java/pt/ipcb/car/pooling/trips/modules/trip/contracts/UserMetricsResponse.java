package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserMetricsResponse {
    private long totalTrips;
    private BigDecimal totalEarnings;
    private BigDecimal totalSpend;
    private BigDecimal totalKm;
}
