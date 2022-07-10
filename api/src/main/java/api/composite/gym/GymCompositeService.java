package api.composite.gym;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "gym composite service", description = "REST API for composite gym information.")
public interface GymCompositeService {
    @ApiOperation(
            value = "${api.gym-composite.get-composite-gym.description}",
            notes = "${api.gym-composite.get-composite-gym.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    /*
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
