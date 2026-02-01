package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.BookingStatusEntity;

import java.util.Optional;

@Repository
public interface BookingStatusRepository extends JpaRepository<BookingStatusEntity, Long> {
    Optional<BookingStatusEntity> findByName(String name);
}
