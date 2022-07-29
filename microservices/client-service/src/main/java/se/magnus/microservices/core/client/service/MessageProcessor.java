package se.magnus.microservices.core.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.client.Client;
import se.magnus.api.core.client.ClientService;
import se.magnus.api.core.program.Program;
import se.magnus.api.core.program.ProgramService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final ClientService clientService;

    @Autowired
    public MessageProcessor(ClientService clientService) {
        this.clientService = clientService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Client> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Client client = event.getData();
                LOG.info("Create client with ID: {}/{}", client.getClientId(), client.getClientId());
                clientService.createClient(client);
                break;

            case DELETE:
                int clientId = event.getKey();
                LOG.info("Delete client with GymID: {}", clientId);
                clientService.deleteClients(clientId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}