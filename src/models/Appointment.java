package models;

import enums.AppointmentStatus;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;
import java.time.LocalDateTime;
import java.util.UUID;

public class Appointment implements Identifiable {
    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String symptoms;

    public Appointment(){

    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(UUID doctorId) {
        this.doctorId = doctorId;
    }

    public Appointment(UUID patientId, UUID doctorId, LocalDateTime appointmentTime, String symptoms)
            throws InvalidInputException {
        ValidationUtil.validateDoctor(doctorId);
        ValidationUtil.validateAppointmentTime(appointmentTime);
        ValidationUtil.validatePatient(patientId);
        this.appointmentId = UUID.randomUUID();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.symptoms = symptoms;
        this.status = AppointmentStatus.SCHEDULED;
    }

    @Override
    public UUID getId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public UUID getPatientId() { return patientId; }
    public UUID getDoctorId() { return doctorId; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public AppointmentStatus getStatus() { return status; }
    public String getSymptoms() { return symptoms; }

    public void setAppointmentTime(LocalDateTime appointmentTime) throws InvalidInputException {
        ValidationUtil.validateAppointmentTime(appointmentTime);
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(AppointmentStatus status) throws InvalidInputException{
        ValidationUtil.validateStatus(status);
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Appointment ID: %s | Patient: %s | Doctor: %s | Time: %s | Status: %s",
                appointmentId.toString().substring(0, 8),
                patientId.toString().substring(0, 8),
                doctorId.toString().substring(0, 8),
                appointmentTime,
                status);
    }
}