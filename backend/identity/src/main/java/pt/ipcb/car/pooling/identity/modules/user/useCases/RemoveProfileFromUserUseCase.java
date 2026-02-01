package pt.ipcb.car.pooling.identity.modules.user.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveProfileFromUserUseCase {

    private final IUserRepository userRepository;
    private final IProfileRepository profileRepository;
    private final UserMapper userMapper;

    public UserResponse execute(UUID userId, String profileName) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var profile = profileRepository.findByName(profileName)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + profileName));

        if (user.getProfiles() != null) {
            user.getProfiles().remove(profile);
        }

        var savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
