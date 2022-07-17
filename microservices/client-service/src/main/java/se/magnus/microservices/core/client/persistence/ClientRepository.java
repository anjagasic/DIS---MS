package se.magnus.microservices.core.client.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ClientRepository extends CrudRepository<ClientEntity, String> {

    Optional<ClientEntity> findByGymId(int gymId);
}