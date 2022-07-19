package se.magnus.api.core.program;

import java.util.List;

import org.springframework.web.bind.annotation.*;

public interface ProgramService {

	/**
     * Sample usage: curl $HOST:$PORT/program?gymId=1
     *
     * @param gymId
     * @return
     */
    @GetMapping(
        value    = "/program",
        produces = "application/json")
    List<Program> getPrograms(@RequestParam(value = "gymId", required = true) int gymId);


    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/program \
     *   -H "Content-Type: application/json" --data \
     *   '{"gymId":123,"programId":456,"name":"Program name"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/program",
            consumes = "application/json",
            produces = "application/json")
    Program createProgram(@RequestBody Program body);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/program?gymId=1
     *
     * @param gymId
     */
    @DeleteMapping(value = "/program")
    void deletePrograms(@RequestParam(value = "gymId", required = true)  int gymId);
}
