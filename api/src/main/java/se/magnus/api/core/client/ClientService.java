package se.magnus.api.core.client;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
    Flux<Client> getClients(@RequestParam(value = "gymId", required = true) int gymId);

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/client \
     *   -H "Content-Type: application/json" --data \
     *   '{"gymId":123,"clientId":456,"fullName":"Test Test","gender":"Male","age":"25"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/client",
            consumes = "application/json",
            produces = "application/json")
    Client createClient(@RequestBody Client body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/client?gymId=1
     *
     * @param gymId
     */
    @DeleteMapping(value = "/client")
    void deleteClients(@RequestParam(value = "gymId", required = true)  int gymId);
}