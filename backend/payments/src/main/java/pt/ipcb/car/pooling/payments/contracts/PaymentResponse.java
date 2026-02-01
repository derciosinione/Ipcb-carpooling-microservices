package pt.ipcb.car.pooling.payments.contracts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pt.ipcb.car.pooling.payments.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private UUID tripId;
    private UUID passengerId;
    private BigDecimal amount;
    private PaymentStatus status;
}
