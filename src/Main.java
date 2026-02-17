import controllers.*;
import models.User;
import enums.Role;
import sql.DatabaseSeeder;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ExecutorService taskExecutor = Executors.newFixedThreadPool(4);
    private static User currentUser = null;

    private static final StaffController staffCtrl = new StaffController();
    private static final PatientController patientCtrl = new PatientController();
    private static final DepartmentController deptCtrl = new DepartmentController();
    private static final WardController wardCtrl = new WardController();
    private static final RoomController roomCtrl = new RoomController();
    private static final AppointmentController appointCtrl = new AppointmentController();
    private static final MedicineController medCtrl = new MedicineController();
    private static final MedicalRecordController recordCtrl = new MedicalRecordController();
    private static final TestReportController testCtrl = new TestReportController();
    private static final BillController billCtrl = new BillController();

    public static void main(String[] args) {
        DatabaseSeeder.seedAdmin();
        Runtime.getRuntime().addShutdownHook(new Thread(taskExecutor::shutdown));

        while (true) {
            if (currentUser == null) {
                performLogin();
            } else {
                handleUserSession();
            }
        }
    }

    private static void performLogin() {
        printHeader("USER LOGIN GATEWAY");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        currentUser = staffCtrl.login(username, password);
        staffCtrl.setCurrentUser(currentUser);
        if (currentUser == null) {
            System.out.println("Invalid Credentials.");
        }
    }

    private static void handleUserSession() {
        displayMainMenu();
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> infrastructureMenu();
                case 2 -> staffMenu();
                case 3 -> patientMenu();
                case 4 -> clinicalMenu();
                case 5 -> pharmacyMenu();
                case 6 -> financeMenu();
                case 7 -> generateSystemReportAsync();
                case 0 -> logout();
                case 99 -> System.exit(0);
                default -> System.out.println("Invalid selection.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void infrastructureMenu() {
        if (!checkAccess(Role.ADMIN)) return;
        printHeader("INFRASTRUCTURE MANAGEMENT");
        System.out.println("1. Add Department | 2.View Departments | 3. Update Department | 4. Delete Department |\n" +
                "5. Add Ward | 6. View Wards | 7. View Ward Details |\n" +
                "8. Update Ward | 9. Delete Ward | 10. Add Room |\n"+
                "11. View Rooms Status | 12. Update Room Details | 13. View Available Rooms |\n" +
                "14. Delete Room | 0. Back");
        int c = Integer.parseInt(scanner.nextLine());
        switch (c) {
            case 1 -> deptCtrl.addDepartment();
            case 2 -> deptCtrl.viewAllDepartments();
            case 3 -> deptCtrl.updateDepartmentDetails();
            case 4 -> deptCtrl.removeDepartment();
            case 5 -> wardCtrl.addWard();
            case 6 -> wardCtrl.viewAllWards();
            case 7 -> wardCtrl.viewWardDetails();
            case 8 -> wardCtrl.updateWard();
            case 9 -> wardCtrl.deleteWard();
            case 10 -> roomCtrl.addRoom();
            case 11 -> roomCtrl.viewRoomStatus();
            case 12 -> roomCtrl.updateRoomDetails();
            case 13 -> roomCtrl.viewAvailableRooms();
            case 14 -> roomCtrl.deleteRoom();
        }
    }

    private static void staffMenu() {
        printHeader("STAFF MANAGEMENT");
        System.out.println("1. Register Doctor | 2. Register Nurse | 3. Register Receptionist |\n" +
                "4. View All Doctors | 5. View All Nurses | 6. View All Receptionists");
        System.out.println("7. Update Specialization | 8. Update Nurse Ward | 9. Update Salary |\n" +
                "10. Update Desk | 11. Toggle Active Status | 12. Display Staff Summary");
        System.out.println("13. Search Staff | 14. View Profile | 15. Update Profile |\n" +
                "16. Change Password | 17. Delete Profile | 0. Back");

        int c = Integer.parseInt(scanner.nextLine());
        if (c >= 1 && c <= 3 || c == 7 || c == 8 || c == 9 || c == 10 || c == 11 || c == 12 || c==17) {
            if (!checkAccess(Role.ADMIN)) return;
        }

        switch (c) {
            case 1 -> staffCtrl.registerDoctor();
            case 2 -> staffCtrl.registerNurse();
            case 3 -> staffCtrl.registerReceptionist();
            case 4 -> staffCtrl.viewAllDoctors();
            case 5 -> staffCtrl.viewAllNurses();
            case 6 -> staffCtrl.viewAllReceptionists();
            case 7 -> staffCtrl.updateDoctorSpecialization();
            case 8 -> staffCtrl.transferNurseWard();
            case 9 -> staffCtrl.updateStaffSalary();
            case 10 -> staffCtrl.updateReceptionistDesk();
            case 11 -> staffCtrl.toggleStaffStatus();
            case 12 -> staffCtrl.displayStaffSummary();
            case 13 -> staffCtrl.searchStaffByName();
            case 14 -> staffCtrl.viewMyProfile();
            case 15 -> staffCtrl.updateMyProfile();
            case 16 -> staffCtrl.changeMyPassword();
            case 17 -> staffCtrl.deleteStaff();
        }
    }

    private static void patientMenu() {
        if (!checkAccess(Role.ADMIN, Role.RECEPTIONIST, Role.DOCTOR)) return;
        printHeader("PATIENT MANAGEMENT");
        System.out.println("1. Admit/Register Patient | 2. View All Patients | 3. Schedule Appointment |\n" +
                "4. View Schedule | 5. Transfer Room | 6.Update Patient Conditions |\n" +
                "7. Discharge Patient | 8. Cancel Appointment | 0. Back");
        int c = Integer.parseInt(scanner.nextLine());
        if((c==5 || c==7) && !checkAccess(Role.DOCTOR, Role.ADMIN))
            return;
        switch (c) {
            case 1 -> patientCtrl.registerAndAdmitPatient();
            case 2 -> patientCtrl.viewAllPatients();
            case 3 -> appointCtrl.scheduleAppointment();
            case 4 -> appointCtrl.viewDoctorSchedule();
            case 5 -> patientCtrl.transferPatientRoom();
            case 6 -> patientCtrl.updatePatientCondition();
            case 7 -> patientCtrl.dischargePatient();
            case 8 -> appointCtrl.cancelAppointment();
        }
    }

    private static void clinicalMenu() {
        printHeader("CLINICAL SERVICES");
        System.out.println("1. Add Medical Record | 2. View History | 3. Generate Test Report | 4. Update/Record Test Results | 5. View Test Reports | 0. Back");
        int c = Integer.parseInt(scanner.nextLine());
        if(c!=2 && !checkAccess(Role.ADMIN, Role.DOCTOR, Role.NURSE))
            return;
        switch (c) {
            case 1 -> recordCtrl.addRecord();
            case 2 -> recordCtrl.viewPatientHistory();
            case 3 -> testCtrl.generateTestReport();
            case 4 -> testCtrl.recordTestResults();
            case 5 -> testCtrl.viewPatientReports();
        }
    }

    private static void pharmacyMenu() {
        if (!checkAccess(Role.ADMIN, Role.DOCTOR, Role.RECEPTIONIST)) return;
        printHeader("PHARMACY MANAGEMENT");
        System.out.println("1. Add Medicine | 2. View Inventory | 3. Update Stock | 4.adma Low Stock | 0. Back");
        int c = Integer.parseInt(scanner.nextLine());
        if(c!=2 && !checkAccess(Role.ADMIN, Role.DOCTOR, Role.NURSE))
            return;
        switch (c) {
            case 1 -> medCtrl.addMedicine();
            case 2 -> medCtrl.viewInventory();
            case 3 -> medCtrl.updateStock();
            case 4 -> medCtrl.checkLowStock();
        }
    }

    private static void financeMenu() {
        if (!checkAccess(Role.ADMIN, Role.RECEPTIONIST)) return;
        printHeader("FINANCE AND BILLING");
        System.out.println("1. Generate Bill and Discharge | 0. Back");
        int c = Integer.parseInt(scanner.nextLine());
        if (c == 1) billCtrl.generateBillAndDischarge();
    }

    private static void logout() {
        currentUser = null;
        staffCtrl.setCurrentUser(null);
        System.out.println("Logged out.");
    }

    private static boolean checkAccess(Role... allowedRoles) {
        for (Role role : allowedRoles) {
            if (currentUser.getRole() == role) return true;
        }
        System.out.println("ACCESS DENIED: Required role not found.");
        return false;
    }

    private static void generateSystemReportAsync() {
        if (!checkAccess(Role.ADMIN)) return;
        String adminName = currentUser.getName();
        taskExecutor.execute(() -> {
            String fileName = "Audit_Report_" + System.currentTimeMillis() + ".txt";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write("Hospital System Audit\n");
                writer.write("Executed by: " + adminName + "\n");
                writer.write("Timestamp: " + LocalDateTime.now() + "\n");
                Thread.sleep(2000);
                System.out.println("Background Report Saved: " + fileName);
            } catch (IOException | InterruptedException e) {
                System.err.println("Report Error: " + e.getMessage());
            }
        });
    }

    private static void displayMainMenu() {
        System.out.println("\nHospital Management System | User: " + currentUser.getName() + " | Role: " + currentUser.getRole());
        System.out.println("1. Infrastructure");
        System.out.println("2. Staff");
        System.out.println("3. Patients");
        System.out.println("4. Clinical");
        System.out.println("5. Pharmacy");
        System.out.println("6. Finance");
        System.out.println("7. Generate Audit (Admin)");
        System.out.println("0. Logout");
        System.out.println("99. Shutdown");
        System.out.print("Select: ");
    }

    private static void printHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }
}