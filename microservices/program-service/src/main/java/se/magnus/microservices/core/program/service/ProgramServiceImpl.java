package se.magnus.microservices.core.program.service;

import org.springframework.dao.DataIntegrityViolationException;
import se.magnus.api.core.program.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.program.Program;
import se.magnus.microservices.core.program.persistence.ProgramEntity;
import se.magnus.microservices.core.program.persistence.ProgramRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class ProgramServiceImpl implements ProgramService {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProgramMapper mapper;
    private final ProgramRepository repository;

    @Autowired
    public ProgramServiceImpl(ServiceUtil serviceUtil, ProgramMapper mapper, ProgramRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public List<Program> getPrograms(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        List<ProgramEntity> entityList = repository.findByGymId(gymId);
        List<Program> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getClients: response size: {}", list.size());

        return list;
    }

    @Override
    public Program createProgram(Program body) {
        try {
            ProgramEntity entity = mapper.apiToEntity(body);
            ProgramEntity newEntity = repository.save(entity);

            LOG.debug("createProgram: created a program entity: {}/{}", body.getGymId(), body.getProgramId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dke) {
            throw new InvalidInputException("Duplicate key, gym Id: " + body.getGymId() + ", program Id:" + body.getProgramId());
        }
    }

    @Override
    public void deletePrograms(int gymId) {
        LOG.debug("deletePrograms: tries to delete program for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId));
    }
}
