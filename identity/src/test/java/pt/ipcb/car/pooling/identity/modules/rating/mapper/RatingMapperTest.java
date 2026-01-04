package pt.ipcb.car.pooling.identity.modules.rating.mapper;

import org.junit.jupiter.api.Test;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RatingMapperTest {

    private final RatingMapper ratingMapper = new RatingMapper();

    @Test
    void toResponse_ShouldMapRatingEntityToRatingResponse() {
        // Arrange
        UUID ratingId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        UUID ratedUserId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        UserEntity rater = UserEntity.builder().id(raterId).Name("Rater").build();
        UserEntity ratedUser = UserEntity.builder().id(ratedUserId).Name("Rated").build();

        RatingEntity entity = RatingEntity.builder()
                .id(ratingId)
                .rater(rater)
                .ratedUser(ratedUser)
                .stars(5)
                .comment("Great!")
                .createdAt(now)
                .build();

        // Act
        RatingResponse response = ratingMapper.toResponse(entity);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(ratingId);
        assertThat(response.raterId()).isEqualTo(raterId);
        assertThat(response.ratedUserId()).isEqualTo(ratedUserId);
        assertThat(response.stars()).isEqualTo(5);
        assertThat(response.comment()).isEqualTo("Great!");
        assertThat(response.createdAt()).isEqualTo(now);
    }
}
