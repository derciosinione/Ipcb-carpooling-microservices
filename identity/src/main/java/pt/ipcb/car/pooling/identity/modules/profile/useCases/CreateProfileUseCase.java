package pt.ipcb.car.pooling.identity.modules.profile.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.exceptions.ResourceAlreadyExistsException;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.request.CreateProfileRequest;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;

import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;

@Service
@RequiredArgsConstructor
public class CreateProfileUseCase {

    private final IProfileRepository repository;
    private final ProfileMapper profileMapper;

    public ProfileResponse execute(CreateProfileRequest request) {

        repository.findByName(request.name())
                .ifPresent(x -> {
                    throw new ResourceAlreadyExistsException();
                });

        var profileEntity = profileMapper.toEntity(request);

        var savedProfile = repository.save(profileEntity);

        return profileMapper.toResponse(savedProfile);
    }
}
