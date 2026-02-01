package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteRatingUseCaseTest {

    @Mock
    private IRatingRepository ratingRepository;

    @InjectMocks
    private DeleteRatingUseCase useCase;

    @Test
    void execute_ShouldDeleteRating() {
        UUID ratingId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        UserEntity rater = UserEntity.builder().id(raterId).build();
        RatingEntity rating = RatingEntity.builder().rater(rater).build();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        useCase.execute(ratingId, raterId);

        verify(ratingRepository).delete(rating);
    }

    @Test
    void execute_ShouldThrowException_WhenUnauthorized() {
        UUID ratingId = UUID.randomUUID();
        UUID raterId = UUID.randomUUID();
        UserEntity rater = UserEntity.builder().id(raterId).build();
        RatingEntity rating = RatingEntity.builder().rater(rater).build();

        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertThatThrownBy(() -> useCase.execute(ratingId, UUID.randomUUID()))
                .isInstanceOf(ActionNotAllowedException.class);
    }
}
