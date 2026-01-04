package pt.ipcb.car.pooling.identity.modules.user.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;
import pt.ipcb.car.pooling.identity.exceptions.ResourceAlreadyExistsException;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final IUserRepository repository;
    private final IProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse execute(RegisterUserRequest request, java.util.Set<String> profileNames) {

        var userEntity = userMapper.toEntity(request);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        if (profileNames != null && !profileNames.isEmpty()) {

            var profiles = profileNames.stream()
                    .map(name -> profileRepository.findByName(name)
                            .orElseThrow(() -> new ResourceNotFoundException("Profile not found: " + name)))
                    .collect(java.util.stream.Collectors.toSet());

            userEntity.setProfiles(profiles);
        }

        repository.findByUsernameOrEmail(userEntity.getUsername(), userEntity.getEmail())
                .ifPresent(user -> {
                    throw new ResourceAlreadyExistsException();
                });

        var savedUser = repository.save(userEntity);
        return userMapper.toResponse(savedUser);
    }
}
