package pt.ipcb.car.pooling.identity.modules.rating.contracts.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateRatingRequest(
        @NotNull UUID raterId,
        @NotNull UUID ratedUserId,
        @Min(0) @Max(5) Integer stars,
        String comment) {
}
