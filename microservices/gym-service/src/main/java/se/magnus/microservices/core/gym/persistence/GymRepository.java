package se.magnus.microservices.core.gym.persistence;

import org.springframework.data.repository.*;

import java.util.Optional;

public interface GymRepository extends PagingAndSortingRepository<GymEntity, String> {

    Optional<GymEntity> findByGymId(int gymId);
}
