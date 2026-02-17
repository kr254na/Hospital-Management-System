package models;

import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;

import java.time.LocalDateTime;
import java.util.UUID;

public class MedicalRecord implements Identifiable {
    private final UUID recordId;
    private final UUID patientId;
    private final UUID doctorId;
    private final UUID appointmentId;
    private String diagnosis;
    private String treatmentPlan;
    private String prescription;
    private final LocalDateTime lastUpdated;

    public MedicalRecord(UUID patientId, UUID doctorId, UUID appointmentId,
                         String diagnosis, String treatmentPlan, String prescription)
            throws InvalidInputException
    {
        ValidationUtil.validatePatient(patientId);
        ValidationUtil.validateDoctor(doctorId);
        ValidationUtil.validateAppointment(appointmentId);
        ValidationUtil.validateDiagnosis(diagnosis);
        ValidationUtil.validateTreatmentPlan(treatmentPlan);
        ValidationUtil.validatePrescription(prescription);
        this.recordId = UUID.randomUUID();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.treatmentPlan = treatmentPlan;
        this.prescription = prescription;
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public UUID getId() {
        return recordId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) throws InvalidInputException {
        ValidationUtil.validateDiagnosis(diagnosis);
        this.diagnosis = diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) throws InvalidInputException {
        ValidationUtil.validateTreatmentPlan(treatmentPlan);
        this.treatmentPlan = treatmentPlan;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) throws InvalidInputException {
        ValidationUtil.validatePrescription(prescription);
        this.prescription = prescription;
    }

    public LocalDateTime getLastUpdated() throws InvalidInputException {
        return lastUpdated;
    }
}