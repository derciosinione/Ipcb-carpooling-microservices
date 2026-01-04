package pt.ipcb.car.pooling.identity.modules.profile.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceNotFoundException;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProfileByIdUseCase {
    final IProfileRepository repository;
    final ProfileMapper profileMapper;

    public ProfileResponse execute(UUID id) {
        var profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Profile with id %s not found", id)));

        return profileMapper.toResponse(profile);
    }
}
