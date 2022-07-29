package se.magnus.microservices.core.program.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.program.Program;
import se.magnus.api.core.program.ProgramService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final ProgramService programService;

    @Autowired
    public MessageProcessor(ProgramService programService) {
        this.programService = programService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Program> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Program program = event.getData();
                LOG.info("Create program with ID: {}/{}", program.getProgramId(), program.getProgramId());
                programService.createProgram(program);
                break;

            case DELETE:
                int programId = event.getKey();
                LOG.info("Delete programs with GymID: {}", programId);
                programService.deletePrograms(programId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}