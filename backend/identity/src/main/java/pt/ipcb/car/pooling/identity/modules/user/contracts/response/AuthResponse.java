package pt.ipcb.car.pooling.identity.modules.user.contracts.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record AuthResponse(UUID id, String email, String token, List<String> roles) {
}
