package se.magnus.microservices.core.gym.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "gyms")
public class GymEntity {

    @Id
    private int id;

    @Version
    private int version;

    @Indexed(unique = true)
    private int gymId;
    private String name;
    private String address;


    public GymEntity() {
    }

    public GymEntity(int id, int version, int gymId, int programId, String name, String address) {
        this.id = id;
        this.version = version;
        this.gymId = gymId;
        this.name = name;
        this.address = address;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
