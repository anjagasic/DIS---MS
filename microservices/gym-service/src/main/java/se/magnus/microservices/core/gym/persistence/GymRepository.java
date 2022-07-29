package se.magnus.microservices.core.gym.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GymRepository extends ReactiveCrudRepository<GymEntity, String> {

    Mono<GymEntity> findByGymId(int gymId);
}
