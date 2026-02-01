package pt.ipcb.car.pooling.vehicles.modules.models.contracts.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateModelRequest {
    @NotBlank(message = "Name cannot be empty or blank")
    private String name;

    @NotNull(message = "Brand ID cannot be null")
    private UUID brandId;
}
