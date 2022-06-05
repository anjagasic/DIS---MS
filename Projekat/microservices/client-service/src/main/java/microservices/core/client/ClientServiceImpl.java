package microservices.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import util.http.ServiceUtil;
import api.core.client.*

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClientServiceImpl implements ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public ClientServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Client> getClients(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        if (gymId == 113) {
            LOG.debug("No client found for gymId: {}", gymId);
            return  new ArrayList<>();
        }

        List<Client> list = new ArrayList<>();
        list.add(new Client(gymId, 1, "Milica Gasic", "Zenski", "22", serviceUtil.getServiceAddress()));
        list.add(new Client(gymId, 2,  "Zorana Ralic", "Zenski", "25", serviceUtil.getServiceAddress()));
        list.add(new Client(gymId, 3,  "Tamara Savic", "Zenski", "26", serviceUtil.getServiceAddress()));

        LOG.debug("/client response size: {}", list.size());

        return list;
    }
}
