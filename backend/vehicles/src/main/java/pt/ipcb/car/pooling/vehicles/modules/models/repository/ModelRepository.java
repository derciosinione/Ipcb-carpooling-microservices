package pt.ipcb.car.pooling.vehicles.modules.models.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.vehicles.modules.models.entities.ModelEntity;

import java.util.UUID;

public interface ModelRepository extends JpaRepository<ModelEntity, UUID> {
    java.util.List<ModelEntity> findByBrandId(UUID brandId);
}
