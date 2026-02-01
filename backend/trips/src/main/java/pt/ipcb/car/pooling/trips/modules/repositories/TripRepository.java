package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.TripEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, UUID> {
    List<TripEntity> findByDriverId(UUID driverId);

    @Query("SELECT t FROM TripEntity t WHERE t.status.name = 'OPEN' AND t.departureTime > CURRENT_TIMESTAMP AND t.availableSeats > 0 ")
    List<TripEntity> findAvailableTrips();

    @Query("SELECT t FROM TripEntity t WHERE " +
        "LOWER(t.origin) like LOWER(CONCAT('%', :origin, '%') ) AND " +
        "LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%') ) and " +
        "t.status.name = 'OPEN' and " +
        "t.departureTime > CURRENT_TIMESTAMP  AND " +
        "t.availableSeats >= :seatsNeeded")
    List<TripEntity> searchTrips(String origin, String destination, Integer seatsNeeded);
}
