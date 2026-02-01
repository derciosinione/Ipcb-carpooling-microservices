package pt.ipcb.car.pooling.trips.modules.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.trips.modules.entities.ExpenseEntity;

import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, UUID> {
}
