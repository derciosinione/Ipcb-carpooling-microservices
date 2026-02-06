package pt.ipcb.car.pooling.gps.modules.location.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveUserLocationRequest {
    @NotBlank
    private String label;

    @NotNull
    private Double lat;

    @NotNull
    private Double lon;
}
