package pt.ipcb.car.pooling.identity.modules.user.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddProfileToUserUseCaseTest {

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IProfileRepository profileRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AddProfileToUserUseCase useCase;

    @Test
    void execute_ShouldAddProfile() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setProfiles(new HashSet<>());
        ProfileEntity profile = new ProfileEntity();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.findByName("P1")).thenReturn(Optional.of(profile));
        when(userMapper.toResponse(any())).thenReturn(null);

        useCase.execute(userId, "P1");

        assertThat(user.getProfiles()).contains(profile);
        verify(userRepository).save(user);
    }
}
