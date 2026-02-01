package pt.ipcb.car.pooling.identity.modules.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ipcb.car.pooling.identity.modules.rating.entities.RatingEntity;
import pt.ipcb.car.pooling.identity.modules.user.entities.UserEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRatingRepository extends JpaRepository<RatingEntity, UUID> {
    List<RatingEntity> findAllByRatedUser(UserEntity ratedUser);
}
