package models;

import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import utility.ValidationUtil;
import java.time.LocalDate;
import java.util.UUID;

public class Nurse extends Employee {
    private UUID wardId;

    public Nurse(){
        super();
    }
    public Nurse(String name, String contact, Gender gender, LocalDate dob,
                 String username, String password, double salary, LocalDate joiningDate, UUID wardId)
            throws InvalidInputException {
        super(name,contact,gender,dob,username,password,Role.NURSE,salary,joiningDate);
        ValidationUtil.validateWard(wardId);
        this.wardId = wardId;
    }

    public UUID getWardId() {
        return wardId;
    }

    public void setWardId(UUID wardId) throws InvalidInputException{
        ValidationUtil.validateWard(wardId);
        this.wardId = wardId;
    }

    @Override
    public String toString() {
        return super.toString() + ", Assigned Ward: " + wardId.toString().substring(0, 8) + "...";
    }
}