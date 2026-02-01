package pt.ipcb.car.pooling.vehicles.modules.vehicles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.vehicles.modules.vehicles.entities.VehicleEntity;

import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {
    List<VehicleEntity> findByUserId(UUID userId);
}
