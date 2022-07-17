package se.magnus.api.composite.gym;

public class EmployeeSummary {

    private final String fullName;
    private final Integer employeeId;


    public EmployeeSummary(Integer employeeId, String fullName) {
        this.fullName = fullName;
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }
}
