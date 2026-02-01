package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.BookingEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    List<BookingEntity> findByPassengerId(UUID passengerId);

    List<BookingEntity> findByTripId(UUID tripId);

    @Query("SELECT COALESCE(SUM(b.seats), 0) FROM BookingEntity b WHERE b.trip.id = :tripId AND b.status.name = 'CONFIRMED'")
    Integer sumConfirmedSeatsByTripId(UUID tripId);

    @Query("SELECT COUNT(b) FROM BookingEntity b WHERE b.passengerId = :passengerId AND b.status.name = 'CONFIRMED'")
    long countConfirmedByPassengerId(UUID passengerId);

    @Query("SELECT COALESCE(SUM(b.priceToPay), 0) FROM BookingEntity b WHERE b.passengerId = :passengerId AND b.status.name = 'CONFIRMED'")
    BigDecimal sumPriceToPayByPassengerId(UUID passengerId);

    @Query("SELECT COALESCE(SUM(b.trip.distanceKm), 0) FROM BookingEntity b WHERE b.passengerId = :passengerId AND b.status.name = 'CONFIRMED'")
    BigDecimal sumDistanceByPassengerId(UUID passengerId);
}
