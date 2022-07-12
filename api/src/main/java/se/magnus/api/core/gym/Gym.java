package se.magnus.api.core.gym;

public class Gym {
	
    private final int gymId;
    private final String name;
	private final String address;
    private final String serviceAddress;

    public Gym() {
    	gymId = 0;
        name = null;
        address = null;
        serviceAddress = null;
    }

    public Gym(
		int gymId,
		String name,
		String address,
		String serviceAddress) {
    	
    	this.gymId = gymId;
    	this.name = name;
    	this.address = address;
    	this.serviceAddress = serviceAddress;
    }

    public int getGymId() {
        return gymId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
