package api.composite.gym;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface GymCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/gym-composite/1
     *
     * @param gymId
     * @return the composite gym info, if found, else null
     */
    @GetMapping(
        value    = "/gym-composite/{gymId}",
        produces = "application/json")
    GymAggregate getGym(@PathVariable int gymId);
}
