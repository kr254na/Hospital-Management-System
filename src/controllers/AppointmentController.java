package controllers;

import config.DbConfig;
import models.Appointment;
import models.Doctor;
import models.Patient;
import services.AppointmentService;
import services.DoctorService;
import services.PatientService;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AppointmentController {
    private final Scanner scanner = new Scanner(System.in);
    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public void scheduleAppointment() {
        System.out.println("\n--- Schedule New Appointment ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String pSearch = scanner.nextLine();
            List<Patient> patients = patientService.findByName(pSearch, conn);
            if (patients.isEmpty()) {
                System.out.println("Patient not found. Register patient first.");
                return;
            }
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            System.out.print("Select Patient: ");
            UUID patientId = patients.get(Integer.parseInt(scanner.nextLine()) - 1).getId();
            System.out.print("Search Doctor Name: ");
            String dSearch = scanner.nextLine();
            List<Doctor> doctors = doctorService.searchByName(dSearch, conn);
            if (doctors.isEmpty()) {
                System.out.println("Doctor not found.");
                return;
            }
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
            }
            System.out.print("Select Doctor: ");
            UUID doctorId = doctors.get(Integer.parseInt(scanner.nextLine()) - 1).getId();
            System.out.print("Enter Date and Time (dd-MM-yyyy HH:mm): ");
            LocalDateTime dateTime = LocalDateTime.parse(scanner.nextLine(), formatter);
            System.out.print("Reason for Visit: ");
            String reason = scanner.nextLine();
            Appointment appointment = new Appointment(patientId, doctorId, dateTime, reason);
            appointmentService.bookAppointment(appointment, conn);
            System.out.println("Appointment scheduled successfully for " + dateTime);

        } catch (Exception e) {
            System.err.println("Failed to schedule: " + e.getMessage());
        }
    }

    public void viewDoctorSchedule() {
        System.out.println("\n--- View Doctor Schedule ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Doctor's Name to view schedule: ");
            String name = scanner.nextLine();
            List<Doctor> doctors = doctorService.searchByName(name, conn);

            if (doctors.isEmpty()) return;
            UUID doctorId = doctors.get(0).getId();

            List<Appointment> schedule = appointmentService.getScheduleByDoctor(doctorId, conn);
            System.out.printf("%-20s | %-20s | %-20s | %-20s | %-15s\n", "Date/Time", "Patient", "Doctor", "Reason", "Status");
            System.out.println("-".repeat(120));
            for (Appointment a : schedule) {
                System.out.printf("%-20s | %-20s | %-20s | %-20s | %-15s\n",
                        a.getAppointmentTime().format(formatter),
                        patientService.findById(a.getPatientId(),conn).get().getName(),
                        doctorService.findById(a.getDoctorId(),conn).get().getName(),
                        a.getSymptoms(), a.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void cancelAppointment() {
        System.out.println("\n--- Cancel Appointment ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Appointment> appointments = appointmentService.findActiveAppointments(conn);
            if (appointments.isEmpty()) {
                System.out.println("No active appointments found in the system.");
                return;
            }
            System.out.printf("%-5s | %-15s | %-15s | %-20s\n", "S.No", "DateTime", "Patient Name", "Doctor Name", "Disease");
            System.out.println("-".repeat(60));
            for (int i = 0; i < appointments.size(); i++) {
                Appointment app = appointments.get(i);
                System.out.printf("%-5d | %-15s | %-15s | %-20s\n",
                        (i + 1),
                        app.getAppointmentTime(),
                        patientService.findById(app.getPatientId(),conn).get().getName(),
                        doctorService.findById(app.getDoctorId(),conn).get().getName());
            }
            System.out.print("\nSelect Appointment S.No to CANCEL: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > appointments.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Appointment selected = appointments.get(choice - 1);
            String patientName = patientService.findById(selected.getPatientId(),conn).get().getName();
            System.out.print("Confirm cancellation for " + patientName + "? (y/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.println("Operation aborted.");
                return;
            }
            appointmentService.cancelAppointment(selected.getId(), conn);
            System.out.println("Appointment for " + patientName + " has been cancelled.");
        } catch (NumberFormatException e) {
            System.err.println("Error: Please enter a valid number for selection.");
        } catch (Exception e) {
            System.err.println("Cancellation failed: " + e.getMessage());
        }
    }
}