package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TripDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateTripRequest {
        private String driverId;
        private String vehicleId;
        private String origin;
        private String destination;
        private String description;
        private LocalDateTime departureTime;
        private Integer availableSeats;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TripResponse {
        private String id;
        private String driverId;
        private String vehicleId;
        private String origin;
        private String destination;
        private String description;
        private LocalDateTime departureTime;
        private Integer availableSeats;
        private Integer confirmedSeats;
        private Integer totalTravelers;
        private BigDecimal totalCost;
        private BigDecimal costPerSeat;
        private String status;
    }
}
