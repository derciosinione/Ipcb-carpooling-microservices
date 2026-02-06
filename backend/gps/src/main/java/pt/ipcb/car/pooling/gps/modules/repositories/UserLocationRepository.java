package pt.ipcb.car.pooling.gps.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.gps.modules.entities.UserLocationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLocationRepository extends JpaRepository<UserLocationEntity, UUID> {

    List<UserLocationEntity> findTop10ByUserIdOrderByUpdatedAtDesc(UUID userId);

    Optional<UserLocationEntity> findByUserIdAndLabelIgnoreCase(UUID userId, String label);
}
