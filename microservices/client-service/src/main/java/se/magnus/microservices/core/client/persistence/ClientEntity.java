package se.magnus.microservices.core.client.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
@CompoundIndex(name = "gym-cli-id", unique = true, def = "{'gymId': 1, 'clientId' : 1}")
public class ClientEntity {

    @Id
    private String id;

    @Version
    private int version;

    private int gymId;
    private int clientId;
    private String fullName;
    private String gender;
    private String age;


    public ClientEntity() {
    }

    public ClientEntity(int gymId, int clientId, String fullName, String gender, String age) {
        this.gymId = gymId;
        this.clientId = clientId;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
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

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
