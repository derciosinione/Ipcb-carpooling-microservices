package pt.ipcb.car.pooling.identity.modules.user.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.UpdateUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse execute(UUID userId, UpdateUserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.name() != null)
            user.setName(request.name());

        if (request.phone() != null)
            user.setPhone(request.phone());

        if (request.description() != null)
            user.setDescription(request.description());

        var savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}
