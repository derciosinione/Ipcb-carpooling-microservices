package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ActionNotAllowedException;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteRatingUseCase {

    private final IRatingRepository ratingRepository;

    public void execute(UUID ratingId, UUID raterId) {
        var rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getRater().getId().equals(raterId)) {
            throw new ActionNotAllowedException("User not allowed to delete this rating");
        }

        ratingRepository.delete(rating);
    }
}
