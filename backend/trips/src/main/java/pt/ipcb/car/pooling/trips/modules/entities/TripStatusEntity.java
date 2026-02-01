package pt.ipcb.car.pooling.trips.modules.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "trip_status")
public class TripStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

}
