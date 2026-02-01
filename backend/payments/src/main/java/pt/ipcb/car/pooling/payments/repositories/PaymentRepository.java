package pt.ipcb.car.pooling.payments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipcb.car.pooling.payments.entities.PaymentEntity;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByTripId(UUID tripId);
}
