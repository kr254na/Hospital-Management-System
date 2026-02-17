package models;

import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.PasswordUtil;
import utility.ValidationUtil;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class User implements Identifiable {
    private UUID userId;
    private String username;
    private String passwordHash;
    private Role role;
    private boolean active;
    private String name;
    private String contact;
    private Gender gender;
    private LocalDate dob;

    public User(){
    }

    public User(String username, String passwordHash, Role role, String name, String contact, Gender gender, LocalDate dob)
            throws InvalidInputException {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validateRole(role);
        ValidationUtil.validateName(name);
        ValidationUtil.validateContact(contact);
        ValidationUtil.validateGender(gender);
        ValidationUtil.validateDob(dob);
        this.userId = UUID.randomUUID();
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.dob = dob;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public UUID getId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() { return name; }

    public String getContact() {
        return contact;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDate getDob() {
        return dob;
    }

    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public Role getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setUsername(String username) throws InvalidInputException {
        ValidationUtil.validateUsername(username);
        this.username = username;
    }

    public void setPassword(String password) throws InvalidInputException {
        ValidationUtil.validatePassword(password);
        this.passwordHash = PasswordUtil.hashPassword(password);
    }

    public void setRole(Role role) throws InvalidInputException {
        ValidationUtil.validateRole(role);
        this.role = role;
    }

    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, this.passwordHash);
    }

    public void setName(String name) throws InvalidInputException{
        ValidationUtil.validateName(name);
        this.name = name;
    }

    public void setContact(String contact) throws InvalidInputException{
        ValidationUtil.validateContact(contact);
        this.contact = contact;
    }

    public void setGender(Gender gender) throws InvalidInputException{
        ValidationUtil.validateGender(gender);
        this.gender = gender;
    }

    public void setDob(LocalDate dob) throws InvalidInputException{
        ValidationUtil.validateDob(dob);
        this.dob = dob;
    }

    public void deactivateUser() {
        this.active = false;
    }

    public void activateUser() {
        this.active = true;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", active=" + active +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", gender=" + gender +
                ", dob=" + dob +
                '}';
    }
}
