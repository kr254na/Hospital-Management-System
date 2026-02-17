package models;

import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;

import java.util.UUID;

public class Department implements Identifiable {
    private UUID deptId;
    private String deptName;
    private UUID headDoctorId;

    public Department(String deptName, UUID headDoctorId) throws InvalidInputException {
        ValidationUtil.validateDepartmentName(deptName);
        this.deptId = UUID.randomUUID();
        this.deptName = deptName;
        this.headDoctorId = headDoctorId;
    }

    @Override
    public UUID getId() {
        return deptId;
    }

    public void setDeptId(UUID deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public UUID getHeadDoctorId() { return headDoctorId; }

    public void setHeadDoctorId(UUID headDoctorId) {
        this.headDoctorId = headDoctorId;
    }

    @Override
    public String toString() {
        return String.format("Dept: %s | ID: %s | HOD ID: %s",
                deptName,
                deptId.toString().substring(0, 8),
                headDoctorId != null ? headDoctorId.toString().substring(0, 8) : "VACANT");
    }

    public String getDeptName() { return deptName; }
}