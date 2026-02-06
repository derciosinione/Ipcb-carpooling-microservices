package pt.ipcb.car.pooling.identity.modules.admin.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserStatusUseCase {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse execute(UUID userId, boolean active) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(active);
        var saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }
}
