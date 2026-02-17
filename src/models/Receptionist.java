package models;

import enums.DeskLocation;
import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import utility.ValidationUtil;
import java.time.LocalDate;

public class Receptionist extends Employee {

    private DeskLocation deskLocation;

    public Receptionist(){
        super();
    }

    public Receptionist(String name, String contact, Gender gender, LocalDate dob,
                        String username, String password, double salary, LocalDate joiningDate, DeskLocation deskLocation)
            throws InvalidInputException {
        super(name,contact,gender,dob,username,password,Role.RECEPTIONIST,salary,joiningDate);
        ValidationUtil.validateDeskLocation(deskLocation);
        this.deskLocation = deskLocation;
    }

    public DeskLocation getDeskLocation() {
        return deskLocation;
    }

    public void setDeskLocation(DeskLocation deskLocation) throws InvalidInputException{
        ValidationUtil.validateDeskLocation(deskLocation);
        this.deskLocation = deskLocation;
    }

    @Override
    public String toString() {
        return super.toString() + ", Desk Location: " + deskLocation;
    }
}