package pt.ipcb.car.pooling.identity.modules.user.contracts.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
                String name,
                @Pattern(regexp = "^\\+?[0-9]*$", message = "Invalid phone number") String phone,
                String description) {
}
