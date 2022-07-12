package se.magnus.api.core.program;

public class Program {
	
	private final int gymId;
    private final int programId;
    private final String name;
    private final String serviceAddress;

    public Program() {
    	gymId = 0;
    	programId = 0;
    	name = null;
        serviceAddress = null;
    }

    public Program(
    	int gymId,
    	int programId,
    	String name,
    	String serviceAddress) {
    	
        this.gymId = gymId;
        this.programId = programId;
        this.name = name;
        this.serviceAddress = serviceAddress;
    }

	public int getGymId() {
		return gymId;
	}

	public int getProgramId() {
		return programId;
	}

	public String getName() {
		return name;
	}

	public String getServiceAddress() {
        return serviceAddress;
    }
}
