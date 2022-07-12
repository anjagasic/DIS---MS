package se.magnus.api.core.employee;

public class Employee {
	
	private final int gymId;
    private final int programId;
    private final String fullName;
    private final String serviceAddress;

    public Employee() {
    	gymId = 0;
    	programId = 0;
    	fullName = null;
        serviceAddress = null;
    }

    public Employee(
    	int gymId,
    	int programId,
    	String fullName,
    	String serviceAddress) {
    	
        this.gymId = gymId;
        this.programId = programId;
        this.fullName = fullName;
        this.serviceAddress = serviceAddress;
    }

	public int getGymId() {
		return gymId;
	}

	public int getProgramId() {
		return programId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getServiceAddress() {
        return serviceAddress;
    }

}
