package api.core.client;

public class Client {
	
	private final int clientId;
    private final int programId;
    private final String fullName;
    private final String gender;
    private final String age;
    private final String serviceAddress;

    public Client() {
		clientId = 0;
    	programId = 0;
    	fullName = null;
    	gender = null;
    	age = null;
        serviceAddress = null;
    }

    public Client(
    	int clientId,
    	int programId,
    	String fullName,
    	String gender,
    	String age,
    	String serviceAddress) {
    	
        this.clientId = clientId;
        this.programId = programId;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
        this.serviceAddress = serviceAddress;
    }

	public int getClientId() {
		return clientId;
	}

	public int getProgramId() {
		return programId;
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

}
