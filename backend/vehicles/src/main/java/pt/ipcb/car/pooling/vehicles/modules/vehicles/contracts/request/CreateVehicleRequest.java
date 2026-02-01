package pt.ipcb.car.pooling.vehicles.modules.vehicles.contracts.request;

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
public class CreateVehicleRequest {

    @NotBlank(message = "Model cannot be empty")
    private String model;

    @NotNull(message = "Brand ID cannot be null")
    private UUID brandId;

    @NotBlank(message = "License plate cannot be empty")
    private String licensePlate;

    private Integer year;

    private String color;
}
