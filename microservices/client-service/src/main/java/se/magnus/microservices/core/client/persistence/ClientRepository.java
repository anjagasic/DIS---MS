package se.magnus.microservices.core.client.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ClientRepository extends ReactiveCrudRepository<ClientEntity, String> {

    Flux<ClientEntity> findByGymId(int gymId);
}