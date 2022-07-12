package se.magnus.api.core.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ClientService {

    /**
     * Sample usage: curl $HOST:$PORT/client?gymId=1
     *
     * @param gymId
     * @return
     */
    @GetMapping(
        value    = "/client",
        produces = "application/json")
    List<Client> getClients(@RequestParam(value = "gymId", required = true) int gymId);
}