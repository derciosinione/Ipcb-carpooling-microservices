package pt.ipcb.car.pooling.identity.modules.rating.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.rating.contracts.response.RatingResponse;
import pt.ipcb.car.pooling.identity.modules.rating.mapper.RatingMapper;
import pt.ipcb.car.pooling.identity.modules.rating.repository.IRatingRepository;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRatingsForUserUseCase {

    private final IRatingRepository ratingRepository;
    private final IUserRepository userRepository;
    private final RatingMapper ratingMapper;

    public List<RatingResponse> execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ratingRepository.findAllByRatedUser(user).stream()
                .map(ratingMapper::toResponse)
                .toList();
    }
}
