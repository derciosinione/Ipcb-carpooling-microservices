package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ExpenseDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateExpenseRequest {
        private String tripId;
        private BigDecimal amount;
        private String description;
        private String type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpenseResponse {
        private String id;
        private String tripId;
        private BigDecimal amount;
        private String description;
        private String type;
    }
}
