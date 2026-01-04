package pt.ipcb.car.pooling.identity.modules.profile.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.modules.profile.contracts.response.ProfileResponse;
import pt.ipcb.car.pooling.identity.modules.profile.mapper.ProfileMapper;
import pt.ipcb.car.pooling.identity.modules.profile.repository.IProfileRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllProfileUseCase {
    final IProfileRepository repository;
    final ProfileMapper profileMapper;

    public List<ProfileResponse> execute() {
        return repository.findAll().stream()
                .map(profileMapper::toResponse)
                .toList();
    }
}
