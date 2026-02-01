package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.CreateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.rating.mapper.RatingMapper;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

@Service
@RequiredArgsConstructor
public class CreateRatingUseCase {

    private final IRatingRepository ratingRepository;
    private final IUserRepository userRepository;
    private final RatingMapper ratingMapper;

    public RatingResponse execute(CreateRatingRequest request) {
        if (request.raterId().equals(request.ratedUserId())) {
            throw new IllegalArgumentException("User cannot rate themselves");
        }

        var rater = userRepository.findById(request.raterId())
                .orElseThrow(() -> new ResourceNotFoundException("Rater not found"));

        var ratedUser = userRepository.findById(request.ratedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Rated user not found"));

        var rating = RatingEntity.builder()
                .rater(rater)
                .ratedUser(ratedUser)
                .stars(request.stars())
                .comment(request.comment())
                .build();

        var savedRating = ratingRepository.save(rating);

        return ratingMapper.toResponse(savedRating);
    }
}
