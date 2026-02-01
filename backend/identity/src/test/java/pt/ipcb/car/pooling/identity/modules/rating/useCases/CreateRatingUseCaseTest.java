package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.CreateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.rating.mapper.RatingMapper;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRatingUseCaseTest {

    @Mock
    private IRatingRepository ratingRepository;
    @Mock
    private IUserRepository userRepository;
    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private CreateRatingUseCase createRatingUseCase;

    @Test
    void execute_ShouldCreateRating() {
        UUID raterId = UUID.randomUUID();
        UUID ratedId = UUID.randomUUID();
        CreateRatingRequest request = new CreateRatingRequest(raterId, ratedId, 5, "Good");

        when(userRepository.findById(raterId)).thenReturn(Optional.of(new UserEntity()));
        when(userRepository.findById(ratedId)).thenReturn(Optional.of(new UserEntity()));
        when(ratingRepository.save(any())).thenReturn(new RatingEntity());
        when(ratingMapper.toResponse(any())).thenReturn(RatingResponse.builder().stars(5).build());

        RatingResponse response = createRatingUseCase.execute(request);

        assertThat(response.stars()).isEqualTo(5);
        verify(ratingRepository).save(any());
    }

    @Test
    void execute_ShouldThrowExceptio_WhenSelfRating() {
        UUID id = UUID.randomUUID();
        CreateRatingRequest request = new CreateRatingRequest(id, id, 5, "Self");

        assertThatThrownBy(() -> createRatingUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
