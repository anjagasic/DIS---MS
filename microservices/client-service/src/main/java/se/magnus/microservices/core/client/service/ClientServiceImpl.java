package se.magnus.microservices.core.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.client.ClientService;
import se.magnus.microservices.core.client.persistence.ClientEntity;
import se.magnus.microservices.core.client.persistence.ClientRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

@RestController
public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ClientMapper mapper;
    private final ClientRepository repository;

    @Autowired
    public ClientServiceImpl(
            ServiceUtil serviceUtil,
            ClientMapper mapper,
            ClientRepository repository
    ) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Flux<Client> getClients(int gymId) {
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        return repository.findByGymId(gymId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public Client createClient(Client body) {
        if (body.getGymId() < 1) throw new InvalidInputException("Invalid gymId: " + body.getGymId());
        ClientEntity entity = mapper.apiToEntity(body);
        Mono<Client> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Gym Id: " + body.getGymId() + ", Client Id:" + body.getClientId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public void deleteClients(int gymId) {
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        LOG.debug("deleteClients: tries to delete client for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId)).block();
    }
}
