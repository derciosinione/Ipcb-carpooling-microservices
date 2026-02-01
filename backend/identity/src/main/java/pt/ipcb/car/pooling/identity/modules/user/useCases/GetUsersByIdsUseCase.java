package pt.ipcb.car.pooling.identity.modules.user.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUsersByIdsUseCase {

    private final IUserRepository repository;
    private final UserMapper userMapper;

    public List<UserResponse> execute(List<UUID> ids) {
        return repository.findAllById(ids).stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
