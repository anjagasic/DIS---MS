package se.magnus.microservices.core.gym.service;

import reactor.core.publisher.Mono;
import com.mongodb.DuplicateKeyException;
import se.magnus.api.core.gym.GymService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.gym.Gym;
import se.magnus.microservices.core.gym.persistence.GymEntity;
import se.magnus.microservices.core.gym.persistence.GymRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import static reactor.core.publisher.Mono.error;

@RestController
public class GymServiceImpl implements GymService {

    private static final Logger LOG = LoggerFactory.getLogger(GymServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final GymMapper mapper;
    private final GymRepository repository;

    @Autowired
    public GymServiceImpl(ServiceUtil serviceUtil, GymMapper mapper, GymRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }


    @Override
    public Mono<Gym> getGym(int gymId) {
        if (gymId < 1)
            throw new InvalidInputException("Invalid gymId: " + gymId);

        return repository.findByGymId(gymId)
                .switchIfEmpty(error(new NotFoundException("No gym found for gymId: " + gymId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public Gym createGym(Gym body) {
        if (body.getGymId() < 1) throw new InvalidInputException("Invalid gymId: " + body.getGymId());

        GymEntity entity = mapper.apiToEntity(body);
        Mono<Gym> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Gym Id: " + body.getGymId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public void deleteGym(int gymId) {
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        LOG.debug("deleteGym: tries to delete an entity with gymId: {}", gymId);
        repository.findByGymId(gymId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();

    }
}
