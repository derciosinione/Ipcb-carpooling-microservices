package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.request.UpdateRatingRequest;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.mapper.RatingMapper;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateRatingUseCase {

    private final IRatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    public RatingResponse execute(UUID ratingId, UpdateRatingRequest request) {
        var rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getRater().getId().equals(request.raterId())) {
            throw new ActionNotAllowedException("User not allowed to update this rating");
        }

        if (request.stars() != null) {
            rating.setStars(request.stars());
        }
        if (request.comment() != null) {
            rating.setComment(request.comment());
        }

        var savedRating = ratingRepository.save(rating);
        return ratingMapper.toResponse(savedRating);
    }
}
