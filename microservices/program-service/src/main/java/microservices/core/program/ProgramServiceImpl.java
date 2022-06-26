package microservices.core.program;

import api.core.program.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import api.core.program.Program;
import util.exceptions.InvalidInputException;
import util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProgramServiceImpl implements ProgramService {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public ProgramServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Program> getPrograms(int gymId) {

        if (gymId < 1) throw new InvalidInputException("Invalid gymId: " + gymId);

        if (gymId == 113) {
            LOG.debug("No program found for gymId: {}", gymId);
            return  new ArrayList<>();
        }

        List<Program> list = new ArrayList<>();
        list.add(new Program(gymId, 1, "Program 1", serviceUtil.getServiceAddress()));
        list.add(new Program(gymId, 2,  "Program 2", serviceUtil.getServiceAddress()));

        LOG.debug("/program response size: {}", list.size());

        return list;
    }
}
