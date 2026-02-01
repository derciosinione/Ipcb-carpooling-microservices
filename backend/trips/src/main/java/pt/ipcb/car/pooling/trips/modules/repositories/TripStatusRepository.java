package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.ExpenseEntity;
import pt.ipcb.car.pooling.trips.modules.entities.TripStatusEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripStatusRepository extends JpaRepository<TripStatusEntity, Long> {
    Optional<TripStatusEntity> findByName(String name);
}
