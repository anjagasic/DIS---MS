package se.magnus.microservices.core.client.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ClientRepository extends CrudRepository<ClientEntity, String> {

    List<ClientEntity> findByGymId(int gymId);
    ClientEntity findByClientId(int clientId);
}