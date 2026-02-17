package controllers;

import config.DbConfig;
import enums.Gender;
import models.Doctor;
import models.Patient;
import models.Room;
import services.DoctorService;
import services.PatientService;
import services.RoomService;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class PatientController {
    private final Scanner scanner = new Scanner(System.in);
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();
    private final RoomService roomService = new RoomService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void registerAndAdmitPatient() {
        System.out.println("\n--- New Patient Admission ---");

        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Contact Number: ");
            String contact = scanner.nextLine();
            System.out.print("Medical History / Disease: ");
            String disease = scanner.nextLine();
            System.out.print("Date of Birth (dd-MM-yyyy): ");
            LocalDate dob = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.println("Gender: (1: Male, 2: Female, 3: Other)");
            int gChoice = Integer.parseInt(scanner.nextLine());
            Gender gender = (gChoice == 1) ? Gender.MALE : (gChoice == 2) ? Gender.FEMALE : Gender.OTHER;
            System.out.print("Enter Assigned Doctor's Name to search: ");
            String docSearch = scanner.nextLine();
            List<Doctor> doctors = doctorService.searchByName(docSearch, conn);
            if (doctors.isEmpty()) {
                System.out.println("No doctors found. Please register a doctor first.");
                return;
            }
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
            }
            System.out.print("Select Doctor (number): ");
            UUID doctorId = doctors.get(Integer.parseInt(scanner.nextLine()) - 1).getId();
            System.out.println("\n--- Room Allocation ---");
            List<Room> availableRooms = roomService.findAvailableRooms(conn);
            if (availableRooms.isEmpty()) {
                System.out.println("Warning: No beds available");
                return;
            } else {
                for (int i = 0; i < availableRooms.size(); i++) {
                    Room r = availableRooms.get(i);
                    System.out.printf("%d. Room %s | Type: %s | Price: Rs %.2f\n",
                            (i + 1), r.getRoomNumber(), r.getRoomType(), r.getPricePerDay());
                }
            }
            System.out.print("Select Room (number) : ");
            int roomChoice = Integer.parseInt(scanner.nextLine());
            UUID roomId = (roomChoice > 0) ? availableRooms.get(roomChoice - 1).getId() : null;
            if(roomId==null){
                System.out.println("Failed to allocate room.");
                return;
            }
            Patient patient = new Patient(name, contact, disease, dob, gender, doctorId, roomId);
            patientService.admitPatient(patient, roomId, conn);

            System.out.println("Patient " + (roomId == null ? "Registered" : "Admitted") + " successfully!");

        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    public void viewAllPatients() {
        System.out.println("\n--- Current Patient List ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Patient> patients = patientService.findAll(conn);
            if (patients.isEmpty()) {
                System.out.println("No records found.");
                return;
            }
            System.out.printf("%-20s | %-10s | %-8s | %-15s | %-15s | %-20s | %-15s \n",
                    "Name","Gender","Age","Contact","Disease","Doctor","Room");
            System.out.println("-".repeat(150));
            for (Patient p : patients) {
                System.out.printf("%-20s | %-10s | %-8s | %-15s | %-15s | %-20s | %-15s\n",
                        p.getName(), p.getGender(), p.getAge(), p.getContact(),
                        p.getDisease(), doctorService.findById(p.getAssignedDoctorId(),conn).get().getName(),
                        roomService.findById(p.getAssignedRoomId(),conn).get().getRoomNumber());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void dischargePatient() {
        System.out.println("\n--- Discharge Patient ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Patient Name to search for discharge: ");
            String name = scanner.nextLine();
            List<Patient> patients = patientService.findByName(name, conn);
            if (patients.isEmpty()) {
                System.out.println("No patient found.");
                return;
            }
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName() + " [ID: " + patients.get(i).getId() + "]");
            }
            System.out.print("Select Patient to discharge (number): ");
            Patient selected = patients.get(Integer.parseInt(scanner.nextLine()) - 1);
            patientService.dischargePatient(selected.getId(), conn);
            System.out.println("Patient " + selected.getName() + " discharged and room bed freed.");

        } catch (Exception e) {
            System.err.println("Discharge failed: " + e.getMessage());
        }
    }

    public void updatePatientCondition() {
        System.out.println("\n--- Update Patient Medical Record ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Patient> patients = patientService.findAll(conn);
            if (patients.isEmpty()) {
                System.out.println("No patients currently registered in the system.");
                return;
            }
            System.out.printf("%-5s | %-15s | %-20s | %-10s\n", "S.No", "Patient ID", "Name", "Room");
            System.out.println("-".repeat(60));
            for (int i = 0; i < patients.size(); i++) {
                Patient p = patients.get(i);
                System.out.printf("%-5d | %-15s | %-20s | %-20s | %-10s\n",
                        (i + 1),
                        p.getId().toString().substring(0, 8),
                        p.getName(),
                        p.getDisease(),
                        roomService.findById(p.getAssignedRoomId(),conn).get().getRoomNumber());
            }
            System.out.print("\nSelect Patient S.No: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Patient selectedPatient = patients.get(choice - 1);
            System.out.print("Enter New Medical Summary for " + selectedPatient.getName() + ": ");
            String newSummary = scanner.nextLine();
            if (newSummary.trim().isEmpty()) {
                System.out.println("Update failed: Summary cannot be empty.");
                return;
            }
            patientService.updateCondition(selectedPatient.getId(), newSummary, conn);
            System.out.println("Medical record updated successfully for " + selectedPatient.getName());
        } catch (NumberFormatException e) {
            System.err.println("Error: Please enter a valid number for selection.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void transferPatientRoom() {
        System.out.println("\n--- Transfer Patient Room ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Patient> activePatients = patientService.findAll(conn);
            if (activePatients.isEmpty()) {
                System.out.println("No patients are currently admitted in rooms.");
                return;
            }
            System.out.println("\nSelect Patient to Transfer:");
            System.out.printf("%-5s | %-20s | %-15s\n", "S.No", "Patient Name", "Current Room");
            System.out.println("-".repeat(45));
            for (int i = 0; i < activePatients.size(); i++) {
                Patient p = activePatients.get(i);
                System.out.printf("%-5d | %-20s | %-15s\n", (i + 1), p.getName(),
                        roomService.findById(p.getAssignedRoomId(),conn).get().getRoomNumber());
            }
            System.out.print("Enter S.No: ");
            int patientIndex = Integer.parseInt(scanner.nextLine()) - 1;
            Patient selectedPatient = activePatients.get(patientIndex);
            List<Room> availableRooms = roomService.findAvailableRooms(conn);
            if (availableRooms.isEmpty()) {
                System.out.println("No vacant rooms available for transfer.");
                return;
            }
            System.out.println("\nSelect New Room:");
            System.out.printf("%-5s | %-12s | %-15s | %-10s\n", "S.No", "Room No", "Type", "Cost/Day");
            System.out.println("-".repeat(50));
            for (int i = 0; i < availableRooms.size(); i++) {
                Room r = availableRooms.get(i);
                System.out.printf("%-5d | %-12s | %-15s | ₹%.2f\n",
                        (i + 1), r.getRoomNumber(), r.getRoomType(), r.getPricePerDay());
            }
            System.out.print("Select New Room S.No: ");
            int roomIndex = Integer.parseInt(scanner.nextLine()) - 1;
            UUID newRoomId = availableRooms.get(roomIndex).getId();
            patientService.transferRoom(selectedPatient.getId(), newRoomId, conn);
            System.out.println("Patient " + selectedPatient.getName() + " transferred to Room " + availableRooms.get(roomIndex).getRoomNumber());
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Invalid selection. Please choose a number from the list.");
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }
    }
}