package pt.ipcb.car.pooling.identity.modules.user.contracts.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record RegisterUserRequest(
                @NotBlank(message = "Username can not be empty or blank") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores") String username,

                @Email(message = "Invalid email provided") String email,

                @Length(min = 10, max = 100) String password,

                String name,

                @Pattern(regexp = "^\\+?[0-9]*$", message = "Invalid phone number") String phone,

                String description) {
}
