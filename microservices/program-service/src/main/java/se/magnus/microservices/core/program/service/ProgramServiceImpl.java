package se.magnus.microservices.core.program.service;

import org.reactivestreams.Publisher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import se.magnus.api.core.program.Program;
import se.magnus.api.core.program.ProgramService;
import se.magnus.microservices.core.program.persistence.ProgramEntity;
import se.magnus.microservices.core.program.persistence.ProgramRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;
import java.util.function.Supplier;

import static java.util.logging.Level.FINE;
@RestController
public class ProgramServiceImpl implements ProgramService {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProgramMapper mapper;
    private final ProgramRepository repository;
    private final Scheduler scheduler;

    @Autowired
    public ProgramServiceImpl(Scheduler scheduler, ServiceUtil serviceUtil, ProgramMapper mapper, ProgramRepository repository) {
        this.scheduler = scheduler;
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Flux<Program> getPrograms(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        List<ProgramEntity> entityList = repository.findByGymId(gymId);
        List<Program> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getClients: response size: {}", list.size());

        LOG.info("Will get programs for gym with id={}", gymId);

        return asyncFlux(() -> Flux.fromIterable(getByGymId(gymId))).log(null, FINE);
    }

    protected List<Program> getByGymId(int gymId) {
        List<ProgramEntity> entityList = repository.findByGymId(gymId);
        List<Program> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        LOG.debug("getPrograms: response size: {}", list.size());
        return list;
    }

    @Override
    public Program createProgram(Program body) {
        if (body.getGymId() < 1) throw new InvalidInputException("Invalid gymId: " + body.getGymId());

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
        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);
        LOG.debug("deletePrograms: tries to delete program for the gym with gymId: {}", gymId);
        repository.deleteAll(repository.findByGymId(gymId));
    }

    private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }
}
