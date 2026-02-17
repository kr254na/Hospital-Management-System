package controllers;

import config.DbConfig;
import models.Patient;
import services.BillService;
import services.PatientService;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class BillController {
    private final Scanner scanner = new Scanner(System.in);
    private final BillService billingService = new BillService();
    private final PatientService patientService = new PatientService();

    public void generateBillAndDischarge() {
        System.out.println("\n--- Billing & Discharge ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Search Patient Name: ");
            String name = scanner.nextLine();
            List<Patient> patients = patientService.findByName(name, conn);
            if (patients.isEmpty()) {
                System.out.println("Patient not found.");
                return;
            }
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            System.out.print("Select Patient: ");
            Patient patient = patients.get(Integer.parseInt(scanner.nextLine()) - 1);
            long daysStayed = 0;
            if (patient.getAssignedRoomId() != null) {
                System.out.print("Enter number of days stayed: ");
                daysStayed = Long.parseLong(scanner.nextLine());
            }
            double totalAmount = billingService.calculateTotalBill(patient, daysStayed, conn);
            printInvoice(patient, daysStayed, totalAmount);
            System.out.print("Process Payment and Discharge? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                billingService.processPaymentAndDischarge(patient.getId(), daysStayed, conn);
                System.out.println("Payment Processed. Patient Discharged and Bed Freed.");
            } else {
                System.out.println("Billing postponed.");
            }
        } catch (Exception e) {
            System.err.println("Billing Error: " + e.getMessage());
        }
    }

    private void printInvoice(Patient p, long days, double total) {
        System.out.println("\n========================================");
        System.out.println("            HOSPITAL INVOICE            ");
        System.out.println("========================================");
        System.out.println("Patient: " + p.getName());
        if (days > 0)
            System.out.println("Days Stayed: " + days);
        System.out.println("----------------------------------------");
        System.out.printf("TOTAL AMOUNT DUE: Rs. %.2f\n", total);
        System.out.println("========================================\n");
    }
}