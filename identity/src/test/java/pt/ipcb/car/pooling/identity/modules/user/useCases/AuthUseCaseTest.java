package pt.ipcb.car.pooling.identity.modules.user.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import pt.ipcb.car.pooling.identity.exceptions.BadCredentialsException;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.AuthRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.AuthResponse;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    void execute_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        // Set @Value fields manually since we are using InjectMocks
        ReflectionTestUtils.setField(authUseCase, "secretKey", "secret");
        ReflectionTestUtils.setField(authUseCase, "tokenIssuer", "issuer");

        AuthRequest request = new AuthRequest("me@test.com", "pass");
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setPassword("encodedPass");

        when(repository.findByEmail("me@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);

        // Act
        AuthResponse result = authUseCase.execute(request);

        // Assert
        assertThat(result.token()).isNotNull();
    }

    @Test
    void execute_ShouldThrowException_WhenUserNotFound() {
        AuthRequest request = new AuthRequest("me@test.com", "pass");
        when(repository.findByEmail("me@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authUseCase.execute(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
