package pt.ipcb.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverPaymentDto {
    private String tripId;
    private String tripRoute;
    private LocalDateTime departureTime;
    private String passengerName;
    private Integer seats;
    private BigDecimal amount;
    private Boolean paid;
    private String paymentReference;
}
