package se.magnus.microservices.core.gym.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.gym.Gym;
import se.magnus.api.core.gym.GymService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final GymService gymService;

    @Autowired
    public MessageProcessor(GymService gymService) {
        this.gymService = gymService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Gym> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Gym gym = event.getData();
                LOG.info("Create gym with ID: {}/{}", gym.getGymId(), gym.getGymId());
                gymService.createGym(gym);
                break;

            case DELETE:
                int gymId = event.getKey();
                LOG.info("Delete gyms with GymID: {}", gymId);
                gymService.deleteGym(gymId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}