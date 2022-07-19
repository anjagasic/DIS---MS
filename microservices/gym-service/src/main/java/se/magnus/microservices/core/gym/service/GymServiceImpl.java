package se.magnus.microservices.core.gym.service;

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
    public Gym getGym(int gymId) {
        if (gymId < 1)
            throw new InvalidInputException("Invalid gymId: " + gymId);

        GymEntity entity = repository.findByGymId(gymId).orElseThrow(() -> new NotFoundException("No meal found for gymId: " + gymId));

        Gym response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getMeal: found gymId: {}", response.getGymId());

        return response;
    }

    @Override
    public Gym createGym(Gym body) {
        try {
            GymEntity entity = mapper.apiToEntity(body);
            GymEntity newEntity = repository.save(entity);

            LOG.debug("createGym: entity created for gymId: {}", body.getGymId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, gym Id: " + body.getGymId());
        }
    }

    @Override
    public void deleteGym(int gymId) {
        LOG.debug("deleteGym: tries to delete an entity with gymId: {}", gymId);
        repository.findByGymId(gymId).ifPresent(repository::delete);

    }
}
