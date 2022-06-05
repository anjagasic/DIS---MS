package microservices.core.gym;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import util.http.ServiceUtil;
import api.core.gym.*

import java.util.ArrayList;
import java.util.List;

@RestController
public class GymServiceImpl implements GymService {

    private static final Logger LOG = LoggerFactory.getLogger(GymServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public GymServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }


}
