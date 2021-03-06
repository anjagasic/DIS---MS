package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee")
@CompoundIndex(name = "gym-emp-id", unique = true, def = "{'gymId': 1, 'employeeId' : 1}")
public class EmployeeEntity {

    @Id
    private String id;

    @Version
    private int version;

    private int gymId;
    private int employeeId;
    private String fullName;


    public EmployeeEntity() {
    }

    public EmployeeEntity(int gymId, int employeeId, String fullName) {
        this.gymId = gymId;
        this.employeeId = employeeId;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
