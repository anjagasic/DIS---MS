package se.magnus.api.composite.gym;

public class ClientSummary {

    private final int clientId;
    private final String fullName;
    private final String gender;
    private final String age;

    public ClientSummary() {
        this.clientId = 0;
        this.fullName = null;
        this.gender = null;
        this.age = null;
    }

    public ClientSummary(
            int clientId,
            String fullName,
            String gender,
            String age) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public int getClientId() {
        return clientId;
    }
}
