package pt.ipcb.car.pooling.identity.modules.user.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.user.contracts.request.RegisterUserRequest;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toResponse_ShouldMapUserEntityToUserResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ProfileEntity profile = ProfileEntity.builder().name("Passageiro").build();
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .Name("Test User")
                .phone("123456789")
                .description("Test Description")
                .profiles(Set.of(profile))
                .build();

        when(profileMapper.toResponse(any())).thenReturn(ProfileResponse.builder().name("Passageiro").build());

        // Act
        UserResponse response = userMapper.toResponse(userEntity);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.username()).isEqualTo("testuser");
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.name()).isEqualTo("Test User");
        assertThat(response.phone()).isEqualTo("123456789");
        assertThat(response.description()).isEqualTo("Test Description");
        assertThat(response.profiles()).hasSize(1);
        assertThat(response.profiles().get(0).name()).isEqualTo("Passageiro");
    }

    @Test
    void toEntity_ShouldMapRegisterUserRequestToUserEntity() {
        // Arrange
        RegisterUserRequest request = RegisterUserRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .name("New User")
                .phone("987654321")
                .description("New Description")
                .build();

        // Act
        UserEntity entity = userMapper.toEntity(request);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getUsername()).isEqualTo("newuser");
        assertThat(entity.getEmail()).isEqualTo("new@example.com");
        assertThat(entity.getPassword()).isEqualTo("password123");
        assertThat(entity.getName()).isEqualTo("New User");
        assertThat(entity.getPhone()).isEqualTo("987654321");
        assertThat(entity.getDescription()).isEqualTo("New Description");
    }
}
