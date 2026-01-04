package pt.ipcb.car.pooling.identity.modules.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    Optional<UserEntity> findByEmail(String email);
}
