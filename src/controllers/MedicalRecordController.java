package controllers;

import config.DbConfig;
import models.Appointment;
import models.Doctor;
import models.MedicalRecord;
import models.Patient;
import services.AppointmentService;
import services.DoctorService;
import services.MedicalRecordService;
import services.PatientService;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MedicalRecordController {
    private final Scanner scanner = new Scanner(System.in);
    private final MedicalRecordService recordService = new MedicalRecordService();
    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorService doctorService = new DoctorService();
    public void addRecord() {
        System.out.println("\n--- Create Medical Entry ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String search = scanner.nextLine();
            List<Patient> patients = patientService.findByName(search, conn);
            if (patients.isEmpty()) {
                System.out.println("Patient not found.");
                return;
            }
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            System.out.print("Select Patient: ");
            Patient patient = patients.get(Integer.parseInt(scanner.nextLine()) - 1);
            List<Appointment> appointments = appointmentService.findByPatientId(patient.getId(), conn);
            if (appointments.isEmpty()) {
                System.out.println("No appointments found for this patient. Cannot create record without appointment.");
                return;
            }
            System.out.println("\n--- Select Linked Appointment ---");
            for (int i = 0; i < appointments.size(); i++) {
                Appointment app = appointments.get(i);
                System.out.println((i + 1) + ". Date: " + app.getAppointmentTime() + " | Status: " + app.getStatus());
            }
            System.out.print("Select Appointment S.No: ");
            Appointment selectedApp = appointments.get(Integer.parseInt(scanner.nextLine()) - 1);
            List<Doctor> doctors = doctorService.findAll(conn);
            System.out.println("\n--- Select Attending Doctor ---");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
            }
            System.out.print("Select Doctor S.No: ");
            Doctor selectedDoc = doctors.get(Integer.parseInt(scanner.nextLine()) - 1);
            System.out.print("Enter Diagnosis: ");
            String diagnosis = scanner.nextLine();
            System.out.print("Enter Treatment/Procedure: ");
            String treatment = scanner.nextLine();
            System.out.print("Clinical Notes: ");
            String notes = scanner.nextLine();
            MedicalRecord record = new MedicalRecord(
                    patient.getId(),
                    selectedDoc.getId(),
                    selectedApp.getId(),
                    diagnosis,
                    treatment,
                    notes
            );
            recordService.saveRecord(record, conn);
            System.out.println("Medical record archived successfully for " + patient.getName());
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error: Invalid selection choice.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void viewPatientHistory() {
        System.out.println("\n--- Patient Medical History ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String search = scanner.nextLine();
            List<Patient> patients = patientService.findByName(search, conn);
            if (patients.isEmpty()) {
                System.out.println("No patient found with that name.");
                return;
            }
            System.out.println("\n--- Select Patient ---");
            for (int i = 0; i < patients.size(); i++) {
                System.out.printf("%d. %s (Contact: %s)\n",
                        (i + 1), patients.get(i).getName(), patients.get(i).getContact());
            }
            System.out.print("Enter Choice (S.No): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Patient selectedPatient = patients.get(choice - 1);
            List<MedicalRecord> history = recordService.findByPatientId(selectedPatient.getId(), conn);
            if (history.isEmpty()) {
                System.out.println("No clinical records found for " + selectedPatient.getName());
                return;
            }
            System.out.println("\nDetailed Medical History for: " + selectedPatient.getName());
            System.out.println("=".repeat(50));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (MedicalRecord r : history) {
                System.out.println("RECORD DATE: " + r.getLastUpdated().format(formatter));
                System.out.println("DIAGNOSIS  : " + r.getDiagnosis());
                System.out.println("TREATMENT  : " + r.getTreatmentPlan());
                System.out.println("PRESCRIPTION : " + r.getPrescription());
                System.out.println("-".repeat(50));
            }
        } catch (NumberFormatException e) {
            System.err.println("Input Error: Please enter a valid serial number.");
        } catch (Exception e) {
            System.err.println("Error fetching history: " + e.getMessage());
        }
    }
}