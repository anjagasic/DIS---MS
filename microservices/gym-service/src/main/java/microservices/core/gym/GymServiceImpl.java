package microservices.core.gym;

import api.core.gym.GymService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import api.core.gym.Gym;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.http.ServiceUtil;

@RestController
public class GymServiceImpl implements GymService {

    private static final Logger LOG = LoggerFactory.getLogger(GymServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public GymServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }


    @Override
    public Gym getGym(int gymId) {
        LOG.debug("/gym return the found gym for gymId={}", gymId);

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        if (gymId == 113) throw new NotFoundException("No gym found for gymId: " + gymId);

        return new Gym(gymId, "name-" + gymId, "address", serviceUtil.getServiceAddress());
    }
}
