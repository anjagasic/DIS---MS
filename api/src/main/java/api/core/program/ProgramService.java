package api.core.program;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
