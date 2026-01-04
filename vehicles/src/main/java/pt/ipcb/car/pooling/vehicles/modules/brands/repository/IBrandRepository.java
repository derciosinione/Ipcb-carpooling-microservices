package pt.ipcb.car.pooling.vehicles.modules.brands.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.vehicles.modules.brands.entities.BrandEntity;

import java.util.Optional;
import java.util.UUID;

public interface IBrandRepository extends JpaRepository<BrandEntity, UUID> {

    Optional<BrandEntity> findByName(String name);
}
