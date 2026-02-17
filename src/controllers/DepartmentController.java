package controllers;

import config.DbConfig;
import models.Department;
import models.Doctor;
import services.DepartmentService;
import services.DoctorService;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class DepartmentController {
    private final Scanner scanner = new Scanner(System.in);
    private final DepartmentService deptService = new DepartmentService();
    private final DoctorService doctorService = new DoctorService();

    public void addDepartment() {
        System.out.println("\n--- Add New Department ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Department Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Doctor's Name to search for Dept Head (or press Enter to skip): ");
            String docSearch = scanner.nextLine();
            UUID headId = null;
            if (!docSearch.isEmpty()) {
                List<Doctor> doctors = doctorService.searchByName(docSearch, conn);
                if (doctors.isEmpty()) {
                    System.out.println("No doctors found with that name. Department created without a head.");
                } else {
                    for (int i = 0; i < doctors.size(); i++) {
                        System.out.println((i + 1) + ". " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
                    }
                    System.out.print("Select Doctor (number): ");
                    int choice = Integer.parseInt(scanner.nextLine());
                    headId = doctors.get(choice - 1).getId();
                }
            }
            Department dept = new Department(name, headId);
            deptService.saveDepartment(dept, conn);
            System.out.println("Department '" + name + "' added successfully!");
        } catch (Exception e) {
            System.err.println("Failed to add department: " + e.getMessage());
        }
    }

    public void updateDepartmentDetails() {
        System.out.println("\n--- Update Department ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Department> depts = deptService.findAll(conn);
            if (depts.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }
            for (int i = 0; i < depts.size(); i++) {
                System.out.println((i + 1) + ". " + depts.get(i).getDeptName());
            }
            System.out.print("Select Department: ");
            int deptChoice = Integer.parseInt(scanner.nextLine());
            Department selectedDept = depts.get(deptChoice - 1);

            System.out.print("Enter New Name (Enter to keep current): ");
            String newName = scanner.nextLine();
            if (!newName.isBlank()) {
                selectedDept.setDeptName(newName);
            }
            System.out.print("Assign New HOD? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                List<Doctor> doctors = doctorService.findAll(conn);
                if (doctors.isEmpty()) {
                    System.out.println("No doctors registered yet to assign as HOD.");
                } else {
                    System.out.println("\n--- Select New HOD ---");
                    for (int i = 0; i < doctors.size(); i++) {
                        System.out.println((i + 1) + ". " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
                    }
                    System.out.print("Enter Doctor S.No: ");
                    int docChoice = Integer.parseInt(scanner.nextLine());
                    selectedDept.setHeadDoctorId(doctors.get(docChoice - 1).getId());
                }
            }
            deptService.updateDepartment(selectedDept, conn);
            System.out.println("Department updated successfully.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void viewAllDepartments() {
        System.out.println("\n--- Hospital Departments ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Department> depts = deptService.findAll(conn);
            if (depts.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }
            System.out.printf("%-5s | %-40s | %-20s\n", "S.No", "Department Name", "Head Doctor");
            System.out.println("---------------------------------------------------------------------------------------");
            for (int i = 0; i < depts.size(); i++) {
                Department d = depts.get(i);
                String headName = "Not Assigned";
                if (d.getHeadDoctorId() != null) {
                    headName = doctorService.findById(d.getHeadDoctorId(), conn)
                            .map(Doctor::getName).orElse("Unknown");
                }
                System.out.printf("%-5d | %-40s | %-20s\n",
                        (i + 1), d.getDeptName(), headName);
            }
        } catch (Exception e) {
            System.err.println("Error fetching departments: " + e.getMessage());
        }
    }

    public void removeDepartment() {
        System.out.println("\n--- Remove Department ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Department> depts = deptService.findAll(conn);
            for (int i = 0; i < depts.size(); i++) {
                System.out.println((i + 1) + ". " + depts.get(i).getDeptName());
            }
            System.out.print("Select Department to remove (number): ");
            int choice = Integer.parseInt(scanner.nextLine());
            Department selected = depts.get(choice - 1);
            deptService.deleteDepartment(selected.getId(), conn);
            System.out.println("Department removed successfully.");
        } catch (Exception e) {
            System.err.println("Deletion failed: " + e.getMessage());
        }
    }

}
