package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class MetricsDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetricsResponse {
        private long totalTrips;
        private BigDecimal totalEarnings;
        private BigDecimal totalSpend;
        private BigDecimal totalKm;
    }
}
