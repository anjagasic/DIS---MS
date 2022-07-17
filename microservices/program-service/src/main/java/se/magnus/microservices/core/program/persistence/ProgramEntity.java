package se.magnus.microservices.core.program.persistence;

import javax.persistence.*;

@Entity
@Table(name = "programs", indexes = {@Index(name = "programs_unique_idx", unique = true, columnList = "gymId,programId")})
public class ProgramEntity { //SQL database

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int gymId;
    private int programId;
    private String name;

    public ProgramEntity() {
    }

    public ProgramEntity(int gymId, int programId, String name) {
        this.gymId = gymId;
        this.programId = programId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
