package controllers;

import config.DbConfig;
import enums.DeskLocation;
import enums.Gender;
import models.*;
import services.*;
import utility.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class StaffController {
    private final Scanner scanner = new Scanner(System.in);
    private final DoctorService doctorService = new DoctorService();
    private final DepartmentService deptService = new DepartmentService();
    private final WardService wardService = new WardService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NurseService nurseService = new NurseService();
    private final ReceptionistService receptionistService = new ReceptionistService();
    private final EmployeeService employeeService = new EmployeeService();
    private final UserService userService = new UserService();
    private User currentUser;

    public User login(String username, String password) {
        try (Connection conn = DbConfig.getConnection()) {
            return userService.authenticate(username, password, conn);
        } catch (Exception e) {
            System.err.println("Login System Error: " + e.getMessage());
            return null;
        }
    }

    public void registerDoctor() {
        System.out.println("\n--- Register New Doctor ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Contact: ");
            String contact = scanner.nextLine();
            System.out.println("Gender: (1: Male, 2: Female, 3: Other)");
            int genderChoice = Integer.parseInt(scanner.nextLine());
            Gender gender = (genderChoice == 1) ? Gender.MALE : (genderChoice == 2) ? Gender.FEMALE : Gender.OTHER;
            List<Department> departments = deptService.findAll(conn);
            if (departments.isEmpty()) {
                System.out.println("No departments found. Create a department first!");
                return;
            }
            System.out.println("Select Department:");
            for (int i = 0; i < departments.size(); i++) {
                System.out.println((i + 1) + ". " + departments.get(i).getDeptName());
            }
            System.out.print("Enter choice (number): ");
            int deptChoice = Integer.parseInt(scanner.nextLine());
            UUID deptId = departments.get(deptChoice - 1).getId();
            System.out.print("Specialization: ");
            String spec = scanner.nextLine();
            System.out.print("Salary: ");
            double salary = Double.parseDouble(scanner.nextLine());
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Date of Birth (dd-MM-yyyy): ");
            String dateInput = scanner.nextLine();
            LocalDate dob = LocalDate.parse(dateInput, formatter);
            Doctor doctor = new Doctor(
                    name, spec, contact, gender, dob,
                    username, PasswordUtil.hashPassword(password), deptId, salary,
                    LocalDate.now()
            );
            doctorService.saveDoctor(doctor, conn);
            System.out.println("Doctor registered successfully!");
        } catch (NumberFormatException e) {
            System.err.println("Invalid input format: Please enter numbers for Salary/Choice.");
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    public void registerNurse() {
        System.out.println("\n--- Register New Nurse ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Contact: ");
            String contact = scanner.nextLine();
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();
            System.out.print("DOB (dd-MM-yyyy): ");
            LocalDate dob = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.println("Gender: (1: Male, 2: Female, 3: Other)");
            int gChoice = Integer.parseInt(scanner.nextLine());
            Gender gender = (gChoice == 1) ? Gender.MALE : (gChoice == 2) ? Gender.FEMALE : Gender.OTHER;
            List<Ward> wards = wardService.findAll(conn);
            if (wards.isEmpty()) {
                System.out.println("No wards found. Create a ward first!");
                return;
            }
            for (int i = 0; i < wards.size(); i++)
                System.out.println((i + 1) + ". " + wards.get(i).getWardName());
            System.out.print("Select Ward (number): ");
            UUID wardId = wards.get(Integer.parseInt(scanner.nextLine()) - 1).getId();
            System.out.print("Salary: ");
            double salary = Double.parseDouble(scanner.nextLine());
            Nurse nurse = new Nurse(name, contact, gender, dob, user, PasswordUtil.hashPassword(pass), salary, LocalDate.now(), wardId);
            nurseService.saveNurse(nurse, conn);
            System.out.println("Nurse registered successfully!");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }

    public void registerReceptionist() {
        System.out.println("\n--- Register New Receptionist ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            System.out.print("Contact: ");
            String contact = scanner.nextLine();
            System.out.println("Gender: (1: Male, 2: Female, 3: Other)");
            int gChoice = Integer.parseInt(scanner.nextLine());
            Gender gender = (gChoice == 1) ? Gender.MALE : (gChoice == 2) ? Gender.FEMALE : Gender.OTHER;
            System.out.print("DOB (dd-MM-yyyy): ");
            LocalDate dob = LocalDate.parse(scanner.nextLine(), formatter);
            System.out.print("Username: ");
            String user = scanner.nextLine();
            System.out.print("Password: ");
            String pass = scanner.nextLine();
            System.out.println("Select Desk Location:");
            for (DeskLocation loc : DeskLocation.values()) System.out.println("- " + loc.name());
            System.out.print("Enter Location Name: ");
            DeskLocation location = DeskLocation.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Salary: ");
            double salary = Double.parseDouble(scanner.nextLine());
            Receptionist recep = new Receptionist(name, contact, gender, dob, user, pass, salary, LocalDate.now(), location);
            receptionistService.saveReceptionist(recep, conn);
            System.out.println("Receptionist registered successfully!");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }

    public void viewAllDoctors() {
        System.out.println("\n--- Registered Doctors ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Department> depts = deptService.findAll(conn);
            for (Department d : depts) {
                List<Doctor> doctors = doctorService.findByDepartment(d.getId(), conn);
                System.out.println("Department: " + d.getDeptName());
                int i=1;
                System.out.printf("%-5s | %-30s | %-5s | %-15s | %-35s | %-25s | %-15s | %-15s | %-18s | %-15s\n","S.No.","Name","Age","Contact","Department","Specialization","Contact","Joining Date","Salary","Active Status");
                System.out.println("-".repeat(200));
                for (Doctor doc : doctors) {
                    System.out.printf("%-5d | %-30s | %-5d | %-15s | %-35s | %-25s | %-15s | %-15s | Rs %-15.2f | %-15s\n",
                            i, doc.getName(), doc.getAge(), doc.getContact(), deptService.findById(doc.getDeptId(),conn).get().getDeptName(),
                            doc.getSpecialization(), doc.getContact(),doc.getDateOfJoining(),doc.getSalary(),doc.isActive()?"Active":"Inactive");
                    i++;
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void viewAllNurses() {
        System.out.println("\n--- Registered Nurses ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);
            for (Ward w : wards) {
                List<Nurse> nurses = nurseService.findByWardId(w.getId(), conn);
                System.out.println("Ward: " + w.getWardName() + " (" + w.getWardNumber() + ")");
                if (nurses.isEmpty()) {
                    System.out.println("  [No nurses assigned]");
                }
                else{
                    System.out.printf("%-5s | %-30s | %-5s | %-15s | %-15s | %-15s | %-15s | %-15s | %-15s","S.No.","Name",
                            "Age","Contact","Ward Number","Ward Name","Joining Date","Salary","Active Status");
                    System.out.println();
                    System.out.println("-".repeat(200));
                    int i=1;
                    for (Nurse nurse : nurses) {
                        System.out.printf("%-5s | %-30s | %-5s | %-15s | %-15s | %-15s | %-15s | Rs %-12.2f | %-15s",
                                i, nurse.getName(), nurse.getAge(), nurse.getContact(),
                                wardService.findById(nurse.getWardId(),conn).get().getWardNumber(),
                                wardService.findById(nurse.getWardId(),conn).get().getWardName(),
                                nurse.getDateOfJoining(),
                                nurse.getSalary(), nurse.isActive()?"Active":"Inactive");
                        i++;
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching nurses: " + e.getMessage());
        }
    }

    public void viewAllReceptionists() {
        System.out.println("\n--- Registered Receptionists ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Receptionist> receptionists = receptionistService.findAll(conn);
            if (receptionists.isEmpty()) {
                System.out.println("No receptionists registered.");
            }
            else{
                System.out.printf("%-5s | %-30s | %-5s | %-15s | %-15s | %-15s | %-15s | %-15s","S.No.","Name",
                        "Age","Contact","Desk Location","Joining Date","Salary","Active Status");
                System.out.println();
                System.out.println("-".repeat(200));
                int i=1;
                for (Receptionist r : receptionists) {
                    System.out.printf("%-5s | %-30s | %-5s | %-15s | %-15s | %-15s | Rs %-12.2f | %-15s\n",
                            i, r.getName(), r.getAge(), r.getContact(), r.getDeskLocation(), r.getDateOfJoining(),
                            r.getSalary(), r.isActive()?"Active":"Inactive");
                    i++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching receptionists: " + e.getMessage());
        }
    }

    public void updateStaffSalary() {
        System.out.println("\n--- Update Employee Salary ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Employee> employees = employeeService.findAll(conn);
            if (employees.isEmpty()) {
                System.out.println("No employees found in the system.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-15s | %-12s\n", "S.No", "Name", "Role", "Current Salary");
            System.out.println("-".repeat(60));
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                System.out.printf("%-5d | %-20s | %-15s | Rs %-12.2f\n",
                        (i + 1), emp.getName(), emp.getRole(), emp.getSalary());
            }
            System.out.print("\nSelect Employee by S.No: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > employees.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Employee selectedEmp = employees.get(choice - 1);
            System.out.print("Enter New Salary for " + selectedEmp.getName() + ": ");
            double newSalary = Double.parseDouble(scanner.nextLine());
            if (newSalary < 0) {
                System.out.println("Update failed: Salary cannot be negative.");
                return;
            }
            employeeService.updateSalary(selectedEmp.getId(), newSalary, conn);
            System.out.println("Salary for " + selectedEmp.getName() + " updated successfully to Rs" + newSalary);
        } catch (NumberFormatException e) {
            System.err.println("Input error: Please enter valid numbers.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void updateDoctorSpecialization() {
        System.out.println("\n--- Update Doctor Specialization ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Doctor> doctors = doctorService.findAll(conn);
            if (doctors.isEmpty()) {
                System.out.println("No doctors registered in the system.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-20s\n", "S.No", "Doctor Name", "Current Specialization");
            System.out.println("-".repeat(55));
            for (int i = 0; i < doctors.size(); i++) {
                Doctor doc = doctors.get(i);
                System.out.printf("%-5d | %-20s | %-20s\n",
                        (i + 1), doc.getName(), doc.getSpecialization());
            }
            System.out.print("\nSelect Doctor by S.No: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > doctors.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Doctor selectedDoc = doctors.get(choice - 1);
            UUID docId = selectedDoc.getId();
            System.out.print("Enter New Specialization for Dr. " + selectedDoc.getName() + ": ");
            String newSpec = scanner.nextLine();

            if (newSpec.trim().isEmpty()) {
                System.out.println("Update cancelled: Specialization cannot be empty.");
                return;
            }
            doctorService.updateSpecialization(docId, newSpec, conn);
            System.out.println("Specialization for Dr. " + selectedDoc.getName() + " updated successfully!");
        } catch (NumberFormatException e) {
            System.err.println("Error: Please enter a valid number for selection.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void updateReceptionistDesk() {
        System.out.println("\n--- Update Receptionist Desk ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Receptionist> receptionists = receptionistService.findAll(conn);
            if (receptionists.isEmpty()) {
                System.out.println("No receptionists registered in the system.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-15s\n", "S.No", "Name", "Current Desk");
            System.out.println("-".repeat(45));
            for (int i = 0; i < receptionists.size(); i++) {
                Receptionist r = receptionists.get(i);
                System.out.printf("%-5d | %-20s | %-15s\n",
                        (i + 1), r.getName(), r.getDeskLocation());
            }
            System.out.print("\nSelect Receptionist S.No: ");
            int recepChoice = Integer.parseInt(scanner.nextLine()) - 1;
            Receptionist selectedRecep = receptionists.get(recepChoice);
            System.out.println("\n--- Select New Desk Location ---");
            DeskLocation[] locations = DeskLocation.values();
            for (int i = 0; i < locations.length; i++) {
                System.out.println((i + 1) + ". " + locations[i].name());
            }
            System.out.print("Enter Choice S.No: ");
            int locChoice = Integer.parseInt(scanner.nextLine()) - 1;
            DeskLocation newLoc = locations[locChoice];
            receptionistService.updateDeskLocation(selectedRecep.getId(), newLoc, conn);
            System.out.println("Desk location for " + selectedRecep.getName() + " updated to " + newLoc);
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void transferNurseWard() {
        System.out.println("\n--- Nurse Ward Transfer ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Nurse> nurses = nurseService.findAll(conn);
            if (nurses.isEmpty()) {
                System.out.println("No nurses found in the system.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-15s\n", "S.No", "Nurse Name", "Current Ward");
            System.out.println("-".repeat(45));
            for (int i = 0; i < nurses.size(); i++) {
                Nurse n = nurses.get(i);
                String wardName = wardService.findById(n.getWardId(), conn)
                        .map(Ward::getWardName).orElse("Unassigned");
                System.out.printf("%-5d | %-20s | %-15s\n", (i + 1), n.getName(), wardName);
            }
            System.out.print("\nSelect Nurse S.No: ");
            int nurseIndex = Integer.parseInt(scanner.nextLine()) - 1;
            Nurse selectedNurse = nurses.get(nurseIndex);
            List<Ward> wards = wardService.findAll(conn);
            if (wards.isEmpty()) {
                System.out.println("No wards available for transfer.");
                return;
            }
            System.out.println("\n--- Select New Ward ---");
            for (int i = 0; i < wards.size(); i++) {
                System.out.println((i + 1) + ". " + wards.get(i).getWardName());
            }
            System.out.print("Select New Ward S.No: ");
            int wardIndex = Integer.parseInt(scanner.nextLine()) - 1;
            UUID newWardId = wards.get(wardIndex).getId();
            nurseService.updateWard(selectedNurse.getId(), newWardId, conn);
            System.out.println("Nurse " + selectedNurse.getName() + " successfully transferred to " + wards.get(wardIndex).getWardName());

        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }
    }

    public void toggleStaffStatus() {
        System.out.println("\n--- Toggle Staff Account Status ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter User UUID: ");
            UUID userId = UUID.fromString(scanner.nextLine());
            System.out.print("Set Active? (true/false): ");
            boolean status = Boolean.parseBoolean(scanner.nextLine());
            userService.updateActiveStatus(userId, status, conn);
            System.out.println("User status updated to " + (status ? "Active" : "Inactive"));
        } catch (Exception e) {
            System.err.println("Status update failed: " + e.getMessage());
        }
    }

    public void deleteStaff() {
        System.out.println("\n--- Permanent Staff Removal ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Employee> employees = employeeService.findAll(conn);
            if (employees.isEmpty()) {
                System.out.println("No staff members found to delete.");
                return;
            }
            System.out.printf("%-5s | %-20s | %-15s | %-12s\n", "S.No", "Name", "Role", "Username");
            System.out.println("-".repeat(60));
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                System.out.printf("%-5d | %-20s | %-15s | %-12s\n",
                        (i + 1), emp.getName(), emp.getRole(), emp.getUsername());
            }
            System.out.print("\nSelect Staff S.No to REMOVE PERMANENTLY: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > employees.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Employee target = employees.get(choice - 1);
            System.out.print("Are you sure you want to delete " + target.getName() + "? (type 'YES' to confirm): ");
            if (!scanner.nextLine().equalsIgnoreCase("YES")) {
                System.out.println("Deletion cancelled.");
                return;
            }
            employeeService.deleteEmployee(target.getId(), conn);
            System.out.println("Staff member [" + target.getName() + "] has been purged from the system.");
        } catch (Exception e) {
            System.err.println("Deletion failed: " + e.getMessage());
        }
    }

    public void searchStaffByName() {
        System.out.print("\nEnter Name to search: ");
        String searchName = scanner.nextLine();

        try (Connection conn = DbConfig.getConnection()) {
            List<User> users = userService.findByName(searchName, conn);

            if (users.isEmpty()) {
                System.out.println("No staff found with that name.");
                return;
            }

            System.out.println("\n--- Search Results ---");
            for (User u : users) {
                System.out.printf("ID: %s | Name: %s | Role: %s\n",
                        u.getId(), u.getName(), u.getRole());
            }
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
        }
    }

    public void displayStaffSummary() {
        System.out.println("\n--- Hospital Staffing Summary ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Department> depts = deptService.findAll(conn);
            for (Department d : depts) {
                int count = doctorService.findByDepartment(d.getId(), conn).size();
                System.out.println(d.getDeptName() + ": " + count + " Doctors");
            }
            List<Ward> wards = wardService.findAll(conn);
            for (Ward w : wards) {
                int count = nurseService.findByWardId(w.getId(), conn).size();
                System.out.println(w.getWardName() + ": " + count + " Nurses");
            }
        } catch (Exception e) {
            System.err.println("Summary fetch failed: " + e.getMessage());
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void changeMyPassword() {
        System.out.println("\n--- Security: Change My Password ---");
        if (currentUser == null) {
                System.out.println("Error: No user currently logged in.");
                return;
        }
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Current Password: ");
            String oldPassword = scanner.nextLine();
            if (!currentUser.getPasswordHash().equals(PasswordUtil.hashPassword(oldPassword))) {
                System.out.println("Incorrect current password. Access denied.");
                return;
            }
            System.out.print("Enter New Password: ");
            String newPass = scanner.nextLine();
            System.out.print("Confirm New Password: ");
            String confirmPass = scanner.nextLine();
            if (!newPass.equals(confirmPass)) {
                System.out.println("Passwords do not match. Try again.");
                return;
            }
            userService.updatePassword(currentUser.getId(),PasswordUtil.hashPassword(newPass), conn);
            System.out.println("Password updated successfully!");
            } catch (Exception e) {
                System.err.println("Reset failed: " + e.getMessage());
            }
        }

    public void viewMyProfile() {
        System.out.println("\n--- My Profile ---");
        if (currentUser == null) {
            System.out.println("Error: No user currently logged in.");
            return;
        }

        try (Connection conn = DbConfig.getConnection()) {
            UUID myId = currentUser.getId();
            System.out.println("Name: " + currentUser.getName());
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println("Gender: "+currentUser.getGender());
            System.out.println("Contact: " + currentUser.getContact());
            System.out.println("DOB: " + currentUser.getDob());
            switch (currentUser.getRole()) {
                case DOCTOR -> {
                    doctorService.findById(myId, conn).ifPresent(doctor -> {
                        try {
                            deptService.findById(doctor.getDeptId(), conn).ifPresent(dept ->
                                    System.out.println("Department: " + dept.getDeptName())
                            );
                        } catch (SQLException e) {
                            System.out.println("Error in checking department:"+e.getMessage());
                        }
                        System.out.println("Specialization: " + doctor.getSpecialization());
                        System.out.println("Salary: ₹" + doctor.getSalary());
                    });
                }
                case NURSE -> {
                    nurseService.findById(myId, conn).ifPresent(nurse -> {
                        try {
                            wardService.findById(nurse.getWardId(), conn).ifPresent(ward ->
                                    System.out.println("Assigned Ward: " + ward.getWardName())
                            );
                        } catch (SQLException e) {
                            System.out.println("Error in checking assigned ward:"+e.getMessage());
                        }
                        System.out.println("Salary: ₹" + nurse.getSalary());
                    });
                }
                case RECEPTIONIST -> {
                    receptionistService.findById(myId, conn).ifPresent(recep -> {
                        System.out.println("Desk Location: " + recep.getDeskLocation());
                        System.out.println("Salary: ₹" + recep.getSalary());
                    });
                }
                default -> System.out.println("Status: Administrative Access");
            }
        } catch (Exception e) {
            System.err.println("Could not retrieve full profile: " + e.getMessage());
        }
    }

    public void updateMyProfile() {
        System.out.println("\n--- Update My Contact Details ---");
        if (currentUser == null) {
            System.out.println("Error: No user currently logged in.");
            return;
        }
        try (Connection conn = DbConfig.getConnection()) {
            System.out.println("Current Name: " + currentUser.getName());
            System.out.print("Enter New Name (or press Enter to keep current): ");
            String newName = scanner.nextLine();
            if (newName.isEmpty())
                newName = currentUser.getName();
            System.out.println("Current Contact: " + currentUser.getContact());
            System.out.print("Enter New Contact (or press Enter to keep current): ");
            String newContact = scanner.nextLine();
            if (newContact.isEmpty()) newContact = currentUser.getContact();
            userService.updatePersonalDetails(currentUser.getId(), newName, newContact, conn);
            currentUser.setName(newName);
            currentUser.setContact(newContact);
            System.out.println("Profile updated successfully!");

        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }
}