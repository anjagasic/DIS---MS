package se.magnus.api.core.employee;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

public interface EmployeeService {

    /**
     * Sample usage: curl $HOST:$PORT/employee?gymId=1
     *
     * @param gymId
     * @return
     */
    @GetMapping(
        value    = "/employee",
        produces = "application/json")
    Flux<Employee> getEmployees(@RequestParam(value = "gymId", required = true) int gymId);

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/comment \
     *   -H "Content-Type: application/json" --data \
     *   '{"mealId":123,"commentId":456,"author":"author","subject":"subj","content":"content", "dateTime":null}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/employee",
            consumes = "application/json",
            produces = "application/json")
    Employee createEmployee(@RequestBody Employee body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/employee?gymId=1
     *
     * @param gymId
     */
    @DeleteMapping(value = "/employee")
    void deleteEmployees(@RequestParam(value = "gymId", required = true)  int gymId);
}
