package pt.ipcb.car.pooling.identity.modules.rating.mapper;

import org.springframework.stereotype.Component;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;

@Component
public class RatingMapper {

    public RatingResponse toResponse(RatingEntity entity) {
        return RatingResponse.builder()
                .id(entity.getId())
                .raterId(entity.getRater().getId())
                .raterName(entity.getRater().getName())
                .ratedUserId(entity.getRatedUser().getId())
                .ratedUserName(entity.getRatedUser().getName())
                .stars(entity.getStars())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
