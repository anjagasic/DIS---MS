package se.magnus.api.composite.gym;

import java.util.List;
import java.util.stream.Collector;

public class ProgramSummary {

    private final String name;
    private final Integer programId;

    public ProgramSummary() {
        this.name = null;
        this.programId = 0;
    }

    public ProgramSummary(Integer programId, String name) {
        this.name = name;
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public Integer getProgramId() {
        return programId;
    }
}
