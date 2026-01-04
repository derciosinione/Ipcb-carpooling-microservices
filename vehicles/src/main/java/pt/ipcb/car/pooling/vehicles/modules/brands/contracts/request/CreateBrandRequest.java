package pt.ipcb.car.pooling.vehicles.modules.brands.contracts.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateBrandRequest(
        @NotBlank(message = "Name can not be empty or blank") String name,
        String description) {
}
