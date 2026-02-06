package pt.ipcb.car.pooling.trips.modules.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID passengerId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private TripEntity trip;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private BookingStatusEntity status;

    private BigDecimal priceToPay = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private Boolean paid = false;

    private LocalDateTime paidAt;

    private String paymentReference;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
