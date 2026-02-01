package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.UpdateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.rating.mapper.RatingMapper;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRatingUseCaseTest {

    @Mock
    private IRatingRepository ratingRepository;
    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private UpdateRatingUseCase useCase;

    @Test
    void execute_ShouldUpdateRating() {
        UUID ratingId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        UpdateRatingRequest request = new UpdateRatingRequest(raterId, 4, "Updated");
        UserEntity rater = UserEntity.builder().id(raterId).build();
        RatingEntity rating = RatingEntity.builder().rater(rater).build();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toResponse(rating)).thenReturn(RatingResponse.builder().stars(4).build());

        RatingResponse response = useCase.execute(ratingId, request);

        assertThat(response.stars()).isEqualTo(4);
        verify(ratingRepository).save(rating);
    }

    @Test
    void execute_ShouldThrowException_WhenUnauthorized() {
        UUID ratingId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        UpdateRatingRequest request = new UpdateRatingRequest(UUID.randomUUID(), 4, "Updated"); // Different rater
        UserEntity rater = UserEntity.builder().id(raterId).build();
        RatingEntity rating = RatingEntity.builder().rater(rater).build();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertThatThrownBy(() -> useCase.execute(ratingId, request))
                .isInstanceOf(ActionNotAllowedException.class);
    }
}
