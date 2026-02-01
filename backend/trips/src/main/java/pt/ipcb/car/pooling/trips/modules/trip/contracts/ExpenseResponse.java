package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import lombok.Data;
import pt.ipcb.car.pooling.trips.modules.trip.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExpenseResponse {
    private UUID id;
    private UUID tripId;
    private BigDecimal amount;
    private String description;
    private ExpenseType type;
    private LocalDateTime registeredAt;
}
