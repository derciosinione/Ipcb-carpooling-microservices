package pt.ipcb.car.pooling.identity.modules.rating.contracts.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RatingResponse(
        UUID id,
        UUID raterId,
        String raterName,
        UUID ratedUserId,
        String ratedUserName,
        Integer stars,
        String comment,
        LocalDateTime createdAt) {
}
