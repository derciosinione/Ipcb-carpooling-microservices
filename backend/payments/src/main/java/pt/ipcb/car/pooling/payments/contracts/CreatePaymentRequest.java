package pt.ipcb.car.pooling.payments.contracts;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID tripId,
        @NotNull UUID passengerId,
        @NotNull BigDecimal amount
) {
}
