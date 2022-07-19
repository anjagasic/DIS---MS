package se.magnus.api.core.employee;

public class Employee {
	
	private final int gymId;
    private final int employeeId;
    private final String fullName;
    private String serviceAddress;

    public Employee() {
    	gymId = 0;
    	employeeId = 0;
    	fullName = null;
        serviceAddress = null;
    }

    public Employee(
    	int gymId,
    	int employeeId,
    	String fullName,
    	String serviceAddress) {
    	
        this.gymId = gymId;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.serviceAddress = serviceAddress;
    }

	public int getGymId() {
		return gymId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getServiceAddress() {
        return serviceAddress;
    }

	public void setServiceAddress(String address) {
		serviceAddress = address;
	}

}
