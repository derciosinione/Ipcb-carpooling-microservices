package pt.ipcb.car.pooling.trips.modules.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "Trips")
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID driverId;

    @NotNull
    private UUID vehicleId;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    private Double originLat;

    private Double originLon;

    private Double destinationLat;

    private Double destinationLon;

    @Column(length = 500)
    private String description;

    @NotNull
    @Future
    private LocalDateTime departureTime;

    @Min(1)
    private Integer availableSeats;

    private BigDecimal totalCost = BigDecimal.ZERO;

    private BigDecimal distanceKm = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TripStatusEntity status;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<ExpenseEntity> expenses = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
