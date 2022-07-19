package se.magnus.api.core.client;

public class Client {
	
	private final int clientId;
    private final int gymId;
    private final String fullName;
    private final String gender;
    private final String age;
    private String serviceAddress;

    public Client() {
		clientId = 0;
    	gymId = 0;
    	fullName = null;
    	gender = null;
    	age = null;
        serviceAddress = null;
    }

    public Client(
    	int clientId,
    	int gymId,
    	String fullName,
    	String gender,
    	String age,
    	String serviceAddress) {
    	
        this.clientId = clientId;
        this.gymId = gymId;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
        this.serviceAddress = serviceAddress;
    }

	public int getClientId() {
		return clientId;
	}

	public int getGymId() {
		return gymId;
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

	public String getServiceAddress() {
        return serviceAddress;
    }

	public void setServiceAddress(String address) {
		serviceAddress = address;
	}

}
