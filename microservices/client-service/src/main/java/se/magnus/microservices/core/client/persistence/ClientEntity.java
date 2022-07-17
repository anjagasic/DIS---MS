package se.magnus.microservices.core.client.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gyms")
@CompoundIndex(name = "gym-cli-id", unique = true, def = "{'gymId': 1, 'clientId' : 1}")
public class ClientEntity {

    @Id
    private int id;

    @Version
    private int version;

    private int gymId;
    private int clientId;
    private String fullName;


    public ClientEntity() {
    }

    public ClientEntity(int id, int version, int gymId, int clientId, String fullName) {
        this.id = id;
        this.version = version;
        this.gymId = gymId;
        this.clientId = clientId;
        this.fullName = fullName;
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

    public int getClientId() {
        return clientId;
    }

    public void setEmployeeId(int clientId) {
        this.clientId = clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
