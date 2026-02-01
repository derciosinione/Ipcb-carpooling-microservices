package pt.ipcb.car.pooling.trips.modules.trip.contracts;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pt.ipcb.car.pooling.trips.modules.trip.enums.ExpenseType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateExpenseRequest {

    @NotNull(message = "Trip ID is mandatory")
    private UUID tripId;

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Expense Type is mandatory (FUEL, TOLL, PARKING, OTHER)")
    private ExpenseType type;
}
