package models;

import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import utility.ValidationUtil;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.UUID;

public class Doctor extends Employee {

    private String specialization;
    private UUID deptId;

    public Doctor(){
        super();
    }
    public Doctor(String name, String specialization,
                  String contact, Gender gender, LocalDate dob, String username, String password, UUID deptId, double salary, LocalDate joiningDate)
            throws InvalidInputException {
        super(name,contact,gender,dob,username,password,Role.DOCTOR,salary,joiningDate);
        ValidationUtil.validateSpecialization(specialization);
        ValidationUtil.validateDepartment(deptId);
        setSpecialization(specialization);
        setDeptId(deptId);
    }

    public String getSpecialization() {
        return specialization;
    }

    public UUID getDeptId(){
        return deptId;
    }

    public void setSpecialization(String specialization)
            throws InvalidInputException {
        ValidationUtil.validateSpecialization(specialization);
        this.specialization = specialization;
    }

    public void setDeptId(UUID deptId) throws InvalidInputException{
        ValidationUtil.validateDepartment(deptId);
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return super.toString() + " [Department: " + deptId + "]" +  " [Specialization: " + specialization + "]" ;
    }
}
