package pt.ipcb.car.pooling.identity.modules.user.contracts.response;

import lombok.Builder;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String name,
        String phone,
        String description,
        java.util.List<ProfileResponse> profiles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
