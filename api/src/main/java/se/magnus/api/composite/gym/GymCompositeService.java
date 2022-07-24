package se.magnus.api.composite.gym;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/gym-composite \
     *   -H "Content-Type: application/json" --data \
     *   '{"gymId":123,"name":"name 11","address":"address 11"}'
     *
     * @param body
     */
    @ApiOperation(
            value = "${api.gym-composite.create-composite-gym.description}",
            notes = "${api.gym-composite.create-composite-gym.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @PostMapping(
            value    = "/gym-composite",
            consumes = "application/json")
    void createCompositeGym(@RequestBody GymAggregate body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/gym-composite/1
     *
     * @param gymId
     */
    @ApiOperation(
            value = "${api.gym-composite.delete-composite-gym.description}",
            notes = "${api.gym-composite.delete-composite-gym.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @DeleteMapping(value = "/gym-composite/{gymId}")
    void deleteCompositeGym(@PathVariable int gymId);
}
