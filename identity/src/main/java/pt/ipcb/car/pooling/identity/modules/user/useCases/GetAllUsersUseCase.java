package pt.ipcb.car.pooling.identity.modules.user.useCases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ipcb.car.pooling.identity.modules.user.contracts.response.UserResponse;
import pt.ipcb.car.pooling.identity.modules.user.mapper.UserMapper;
import pt.ipcb.car.pooling.identity.modules.user.repository.IUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {
    private final IUserRepository repository;
    private final UserMapper userMapper;

    public List<UserResponse> execute() {
        return repository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
