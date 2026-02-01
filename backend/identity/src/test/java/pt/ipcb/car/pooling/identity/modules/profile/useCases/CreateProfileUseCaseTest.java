package pt.ipcb.car.pooling.identity.modules.profile.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;
import pt.ipcb.car.pooling.identity.exceptions.ResourceAlreadyExistsException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProfileUseCaseTest {

    @Mock
    private IProfileRepository repository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private CreateProfileUseCase createProfileUseCase;

    @Test
    void execute_ShouldCreateProfile_WhenProfileDoesNotExist() {
        // Arrange
        CreateProfileRequest request = new CreateProfileRequest("Test", "Desc");
        ProfileEntity entity = new ProfileEntity();
        ProfileEntity savedEntity = new ProfileEntity();
        ProfileResponse response = ProfileResponse.builder().name("Test").build();

        when(repository.findByName("Test")).thenReturn(Optional.empty());
        when(profileMapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(profileMapper.toResponse(savedEntity)).thenReturn(response);

        // Act
        ProfileResponse result = createProfileUseCase.execute(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test");
        verify(repository).save(entity);
    }

    @Test
    void execute_ShouldThrowException_WhenProfileExists() {
        // Arrange
        CreateProfileRequest request = new CreateProfileRequest("Test", "Desc");

        when(repository.findByName("Test")).thenReturn(Optional.of(new ProfileEntity()));

        // Act & Assert
        assertThatThrownBy(() -> createProfileUseCase.execute(request))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }
}
