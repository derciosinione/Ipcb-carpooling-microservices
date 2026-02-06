package pt.ipcb.car.pooling.identity.modules.user.contracts.response;

import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PublicUserResponse(
        UUID id,
        String name,
        String description,
        List<ProfileResponse> profiles,
        LocalDateTime createdAt) {
}
