package pt.ipcb.car.pooling.identity.modules.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.identity.modules.profile.entities.ProfileEntity;

import java.util.Optional;
import java.util.UUID;

public interface IProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    Optional<ProfileEntity> findByName(String name);
}
