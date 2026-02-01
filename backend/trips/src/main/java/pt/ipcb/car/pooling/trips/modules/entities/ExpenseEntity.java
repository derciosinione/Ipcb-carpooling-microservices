package pt.ipcb.car.pooling.trips.modules.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import pt.ipcb.car.pooling.trips.modules.trip.enums.ExpenseType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Expenses")
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripEntity trip;

    private BigDecimal amount;
    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseType type;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
