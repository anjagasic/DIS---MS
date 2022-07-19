package se.magnus.microservices.core.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.client.ClientService;
import se.magnus.microservices.core.client.persistence.ClientEntity;
import se.magnus.microservices.core.client.persistence.ClientRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

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
    public List<Client> getClients(int gymId) {
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        List<ClientEntity> entityList = repository.findByGymId(gymId);
        List<Client> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getClients: response size: {}", list.size());

        return list;
    }

    @Override
    public Client createClient(Client body) {
        try {
            ClientEntity entity = mapper.apiToEntity(body);
            ClientEntity newEntity = repository.save(entity);

            LOG.debug("createClient: created a client entity: {}/{}", body.getGymId(), body.getClientId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dke) {
            throw new InvalidInputException("Duplicate key, gym Id: " + body.getGymId() + ", client Id:" + body.getClientId());
        }
    }

    @Override
    public void deleteClients(int gymId) {
        LOG.debug("deleteClients: tries to delete client for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId));
    }
}
