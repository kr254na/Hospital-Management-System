package models;

import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import utility.ValidationUtil;
import java.time.LocalDate;

public abstract class Employee extends User {
    private double salary;
    private LocalDate dateOfJoining;

    public Employee(){
        super();
    }
    public Employee(String name, String contact, Gender gender, LocalDate dob,
                    String username, String password, Role role,
                    double salary, LocalDate dateOfJoining)
            throws InvalidInputException {
        super(username, password, role, name, contact, gender, dob);
        ValidationUtil.validateSalary(salary);
        ValidationUtil.validateJoiningDate(dob,dateOfJoining);
        this.salary = salary;
        this.dateOfJoining = dateOfJoining;
    }

    public double getSalary() { return salary; }

    public void setSalary(double salary) throws InvalidInputException{
        ValidationUtil.validateSalary(salary);
        this.salary = salary; }

    public LocalDate getDateOfJoining() { return dateOfJoining; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Salary: Rs%.2f | Joined: %s",
                salary, dateOfJoining);
    }
}