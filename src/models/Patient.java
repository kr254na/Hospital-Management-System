package models;

import enums.Gender;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class Patient implements Identifiable {
    private UUID patientId;
    private String name;
    private String disease;
    private String contact;
    private LocalDate dob;
    private Gender gender;
    private UUID assignedDoctorId;
    private UUID assignedRoomId;

    public Patient(String name, String contact, String disease,
                   LocalDate dob, Gender gender, UUID doctorId, UUID roomId) throws InvalidInputException {
        this.patientId = UUID.randomUUID();
        setName(name);
        setContact(contact);
        setDisease(disease);
        setDob(dob);
        setGender(gender);
        this.assignedDoctorId = doctorId;
        this.assignedRoomId = roomId;
    }

    @Override
    public UUID getId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        ValidationUtil.validateName(name);
        this.name = name;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) throws InvalidInputException {
        ValidationUtil.validateDisease(disease);
        this.disease = disease;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) throws InvalidInputException {
        ValidationUtil.validateContact(contact);
        this.contact = contact;
    }

    public LocalDate getDob() {
        return dob;
    }

    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public void setDob(LocalDate dob) throws InvalidInputException {
        ValidationUtil.validateDob(dob);
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) throws InvalidInputException {
        ValidationUtil.validateGender(gender);
        this.gender = gender;
    }

    public UUID getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(UUID assignedDoctorId) throws InvalidInputException {
        ValidationUtil.validateDoctor(assignedDoctorId);
        this.assignedDoctorId = assignedDoctorId;
    }

    public UUID getAssignedRoomId() {
        return assignedRoomId;
    }

    public void setAssignedRoomId(UUID assignedRoomId) throws InvalidInputException {
        ValidationUtil.validateWard(assignedRoomId);
        this.assignedRoomId = assignedRoomId;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", name='" + name + '\'' +
                ", disease='" + disease + '\'' +
                ", contact='" + contact + '\'' +
                ", dob=" + dob +
                ", gender=" + gender +
                ", assignedDoctorId=" + assignedDoctorId +
                ", assignedRoomId=" + assignedRoomId +
                '}';
    }
}