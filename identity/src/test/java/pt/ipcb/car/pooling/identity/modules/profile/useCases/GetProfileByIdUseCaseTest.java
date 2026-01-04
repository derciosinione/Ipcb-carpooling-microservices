package pt.ipcb.car.pooling.identity.modules.profile.useCases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProfileByIdUseCaseTest {

    @Mock
    private IProfileRepository repository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private GetProfileByIdUseCase getProfileByIdUseCase;

    @Test
    void execute_ShouldReturnProfile_WhenFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProfileEntity entity = new ProfileEntity();
        ProfileResponse response = ProfileResponse.builder().id(id).build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(profileMapper.toResponse(entity)).thenReturn(response);

        // Act
        ProfileResponse result = getProfileByIdUseCase.execute(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void execute_ShouldThrowException_WhenNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getProfileByIdUseCase.execute(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
