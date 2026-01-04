package pt.ipcb.car.pooling.identity.modules.profile.mapper;

import org.junit.jupiter.api.Test;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileMapperTest {

    private final ProfileMapper profileMapper = new ProfileMapper();

    @Test
    void toResponse_ShouldMapProfileEntityToProfileResponse() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ProfileEntity entity = ProfileEntity.builder()
                .id(profileId)
                .name("Test Profile")
                .description("Test Description")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        ProfileResponse response = profileMapper.toResponse(entity);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(profileId);
        assertThat(response.name()).isEqualTo("Test Profile");
        assertThat(response.description()).isEqualTo("Test Description");
        assertThat(response.createdAt()).isEqualTo(now);
        assertThat(response.updatedAt()).isEqualTo(now);
    }

    @Test
    void toEntity_ShouldMapCreateProfileRequestToProfileEntity() {
        // Arrange
        CreateProfileRequest request = new CreateProfileRequest("New Profile", "New Description");

        // Act
        ProfileEntity entity = profileMapper.toEntity(request);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("New Profile");
        assertThat(entity.getDescription()).isEqualTo("New Description");
    }
}
