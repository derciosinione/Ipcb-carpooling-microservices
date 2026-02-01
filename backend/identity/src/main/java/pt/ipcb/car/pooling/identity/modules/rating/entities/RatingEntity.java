package pt.ipcb.car.pooling.identity.modules.rating.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity(name = "Ratings")
@AllArgsConstructor
@NoArgsConstructor
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity rater;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity ratedUser;

    @Min(0)
    @Max(5)
    private Integer stars;

    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
