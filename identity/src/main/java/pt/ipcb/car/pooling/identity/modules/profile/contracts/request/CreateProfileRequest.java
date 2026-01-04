package pt.ipcb.car.pooling.identity.modules.profile.contracts.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateProfileRequest(
        @NotBlank(message = "Name can not be empty or blank") String name,
        String description) {
}
