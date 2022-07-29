package se.magnus.api.core.gym;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface GymService {

    /**
     * Sample usage: curl $HOST:$PORT/gym/1
     *
     * @param gymId
     * @return the gym, if found, else null
     */
    @GetMapping(
        value    = "/gym/{gymId}",
        produces = "application/json")
     Mono<Gym> getGym(@PathVariable int gymId);

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/gym \
     *   -H "Content-Type: application/json" --data \
     *   '{"gym":123,"name":"Gym name","address":"Gym address"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/gym",
            consumes = "application/json",
            produces = "application/json")
    Gym createGym(@RequestBody Gym body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/meal/1
     *
     * @param gymId
     */
    @DeleteMapping(value = "/gym/{gymId}")
    void deleteGym(@PathVariable int gymId);
}
