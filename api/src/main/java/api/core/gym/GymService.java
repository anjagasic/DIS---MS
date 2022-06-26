package api.core.gym;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
     Gym getGym(@PathVariable int gymId);
}
