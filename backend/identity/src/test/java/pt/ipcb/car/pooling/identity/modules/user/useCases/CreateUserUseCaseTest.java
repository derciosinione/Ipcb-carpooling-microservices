package pt.ipcb.car.pooling.identity.modules.user.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ipcb.car.pooling.identity.exceptions.ResourceAlreadyExistsException;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private IProfileRepository profileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    void execute_ShouldCreateUser_WhenUserDoesNotExist() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("user")
                .email("email@test.com")
                .password("password")
                .build();
        UserEntity entity = new UserEntity();
        UserEntity savedEntity = new UserEntity();
        UserResponse response = UserResponse.builder().username("user").build();

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(profileRepository.findByName("Passageiro")).thenReturn(Optional.of(new ProfileEntity()));
        when(repository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.empty());
        when(repository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toResponse(savedEntity)).thenReturn(response);

        // Act
        UserResponse result = createUserUseCase.execute(request, Set.of("Passageiro"));

        // Assert
        assertThat(result).isNotNull();
        verify(repository).save(entity);
    }

    @Test
    void execute_ShouldThrowException_WhenUserExists() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder().username("user").build();
        UserEntity entity = new UserEntity();
        entity.setUsername("user");

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(repository.findByUsernameOrEmail(any(), any())).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        assertThatThrownBy(() -> createUserUseCase.execute(request, Set.of()))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }
}
