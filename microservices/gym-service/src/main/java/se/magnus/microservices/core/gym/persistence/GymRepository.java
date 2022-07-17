package se.magnus.microservices.core.gym.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GymRepository extends PagingAndSortingRepository<GymEntity, String> {

    List<GymEntity> findByGymId(int gymId);
}
