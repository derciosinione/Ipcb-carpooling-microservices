package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BookingDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingResponse {
        private String id;
        private String tripId;
        private String passengerId;
        private Integer seats;
        private BigDecimal priceToPay;
        private String status;
    }
}
