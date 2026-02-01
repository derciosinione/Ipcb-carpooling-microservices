package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    List<BookingEntity> findByPassengerId(UUID passengerId);

}
