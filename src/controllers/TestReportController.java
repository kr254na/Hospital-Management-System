package controllers;

import config.DbConfig;
import models.Doctor;
import models.TestReport;
import models.Patient;
import services.DoctorService;
import services.TestReportService;
import services.PatientService;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TestReportController {
    private final Scanner scanner = new Scanner(System.in);
    private final TestReportService testService = new TestReportService();
    private final PatientService patientService = new PatientService();
    private final DoctorService doctorService = new DoctorService();

    public void generateTestReport() {
        System.out.println("\n--- Create Diagnostic Test Report ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String search = scanner.nextLine();
            List<Patient> patients = patientService.findByName(search, conn);
            if (patients.isEmpty()) {
                System.out.println("Patient not found.");
                return;
            }
            System.out.println("\n--- Select Patient ---");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            System.out.print("Choice: ");
            Patient patient = patients.get(Integer.parseInt(scanner.nextLine()) - 1);
            List<Doctor> doctors = doctorService.findAll(conn);
            if (doctors.isEmpty()) {
                System.out.println("No doctors found to order the test.");
                return;
            }
            System.out.println("\n--- Select Ordering Doctor ---");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + " (" + doctors.get(i).getSpecialization() + ")");
            }
            System.out.print("Choice: ");
            Doctor selectedDoc = doctors.get(Integer.parseInt(scanner.nextLine()) - 1);
            System.out.print("Test Name (e.g., CBC, MRI, Glucose): ");
            String testName = scanner.nextLine();
            System.out.print("Test Price: ");
            double cost = Double.parseDouble(scanner.nextLine());
            TestReport report = new TestReport(
                    patient.getId(),
                    selectedDoc.getId(),
                    testName,
                    cost
            );
            testService.saveReport(report, conn);
            System.out.println("Diagnostic order for '" + testName + "' recorded for Dr. " + selectedDoc.getName());
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Selection error: Please choose a valid number from the list.");
        } catch (Exception e) {
            System.err.println("Failed to generate report: " + e.getMessage());
        }
    }

    public void viewPatientReports() {
        System.out.println("\n--- View Patient Diagnostic History ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Patient Name: ");
            String search = scanner.nextLine();
            List<Patient> patients = patientService.findByName(search, conn);
            if (patients.isEmpty()) {
                System.out.println("No patients found with that name.");
                return;
            }
            System.out.println("\nSelect Patient:");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName() + " (ID: " + patients.get(i).getId().toString() + ")");
            }
            System.out.print("Enter Choice (S.No): ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Patient patient = patients.get(choice - 1);
            List<TestReport> reports = testService.findByPatientId(patient.getId(), conn);
            if (reports.isEmpty()) {
                System.out.println("No diagnostic reports found for " + patient.getName());
                return;
            }
            System.out.println("\nDiagnostic History for: " + patient.getName());
            System.out.println("-".repeat(85));
            System.out.printf("%-20s | %-20s | %-15s | %-20s\n", "Report Date", "Test Name", "Status", "Result");
            System.out.println("-".repeat(85));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (TestReport r : reports) {
                String dateStr = r.getReportDate() != null ? r.getReportDate().format(dtf) : "N/A";
                String resultStr = (r.getResult() == null || r.getResult().isEmpty()) ? "PENDING" : r.getResult();
                System.out.printf("%-20s | %-20s | %-15s | %-20s\n",
                        dateStr,
                        r.getTestName(),
                        r.getStatus(),
                        resultStr);
            }
            System.out.println("-".repeat(85));
        } catch (NumberFormatException e) {
            System.err.println("Input Error: Please enter a valid number.");
        } catch (Exception e) {
            System.err.println("System Error: " + e.getMessage());
        }
    }

    public void recordTestResults() {
        System.out.println("\n--- Record Lab Results ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String search = scanner.nextLine();
            List<Patient> patients = patientService.findByName(search, conn);
            if (patients.isEmpty()) {
                System.out.println("Patient not found.");
                return;
            }
            System.out.println("\n--- Select Patient ---");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            System.out.print("Choice: ");
            Patient patient = patients.get(Integer.parseInt(scanner.nextLine()) - 1);
            List<TestReport> pendingTests = testService.findPendingByPatient(patient.getId(), conn);
            if (pendingTests.isEmpty()) {
                System.out.println("No pending laboratory tests found for " + patient.getName());
                return;
            }
            System.out.println("\n--- Select Test to Record Results ---");
            System.out.printf("%-5s | %-30s\n", "S.No", "Test Name");
            System.out.println("-".repeat(45));
            for (int i = 0; i < pendingTests.size(); i++) {
                TestReport tr = pendingTests.get(i);
                System.out.printf("%-5d | %-30s\n",
                        (i + 1), tr.getTestName());
            }
            System.out.print("Choice S.No: ");
            TestReport selectedReport = pendingTests.get(Integer.parseInt(scanner.nextLine()) - 1);
            System.out.print("Enter Findings/Results for " + selectedReport.getTestName() + ": ");
            String findings = scanner.nextLine();
            selectedReport.enterResult(findings);
            testService.updateReport(selectedReport, conn);
            System.out.println("Laboratory results recorded. Test status updated to COMPLETED.");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Invalid selection choice.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }
}