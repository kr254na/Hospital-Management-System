package controllers;

import config.DbConfig;
import models.Medicine;
import services.MedicineService;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MedicineController {
    private final Scanner scanner = new Scanner(System.in);
    private final MedicineService medicineService = new MedicineService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void addMedicine() {
        System.out.println("\n--- Add New Medicine to Stock ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Medicine Name: ");
            String name = scanner.nextLine();
            System.out.print("Quantity in Stock: ");
            int stock = Integer.parseInt(scanner.nextLine());
            System.out.print("Price per Unit: ");
            double price = Double.parseDouble(scanner.nextLine());
            Medicine med = new Medicine(name, price, stock);
            medicineService.saveMedicine(med, conn);
            System.out.println(name + " added to inventory successfully!");
        } catch (Exception e) {
            System.err.println("Failed to add medicine: " + e.getMessage());
        }
    }

    public void viewInventory() {
        System.out.println("\n--- Current Pharmacy Inventory ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Medicine> inventory = medicineService.findAll(conn);
            if (inventory.isEmpty()) {
                System.out.println("No medicines in stock.");
                return;
            }

            System.out.printf("%-20s | %-8s | %-10s \n",
                    "Name", "Stock", "Price");
            System.out.println("-".repeat(75));
            for (Medicine m : inventory) {
                System.out.printf("%-20s | %-8d | Rs %-10.2f \n",
                        m.getName(), m.getStockQuantity(),
                        m.getPricePerUnit());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void updateStock() {
        System.out.println("\n--- Restock Medicine / Update Inventory ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Medicine Name to search: ");
            String name = scanner.nextLine();
            List<Medicine> results = medicineService.searchByName(name, conn);
            if (results.isEmpty()) {
                System.out.println("No medicines found matching: " + name);
                return;
            }
            System.out.println("\nSearch Results:");
            System.out.printf("%-5s | %-20s | %-10s | %-10s\n", "S.No", "Medicine Name", "Current Stock", "Price");
            System.out.println("-".repeat(55));
            for (int i = 0; i < results.size(); i++) {
                Medicine m = results.get(i);
                System.out.printf("%-5d | %-20s | %-13d | ₹%.2f\n",
                        (i + 1), m.getName(), m.getStockQuantity(), m.getPricePerUnit());
            }
            System.out.print("\nSelect Medicine S.No: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > results.size()) {
                System.out.println("Invalid selection. Operation cancelled.");
                return;
            }
            Medicine selected = results.get(choice - 1);
            System.out.print("Enter quantity to ADD to " + selected.getName() + ": ");
            int additionalQty = Integer.parseInt(scanner.nextLine());
            if (additionalQty <= 0) {
                System.out.println("Quantity must be greater than zero.");
                return;
            }
            medicineService.addStock(selected.getId(), additionalQty, conn);
            System.out.println("Stock updated! New total for " + selected.getName() + " is "
                    + (selected.getStockQuantity() + additionalQty));
        } catch (NumberFormatException e) {
            System.err.println("Error: Please enter a numeric value for S.No and Quantity.");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void checkLowStock() {
        System.out.println("\n--- Low Stock Alert (Qty < 10) ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Medicine> lowStock = medicineService.findLowStock(10, conn);
            if (lowStock.isEmpty()) {
                System.out.println("All stock levels are healthy.");
            } else {
                lowStock.forEach(m -> System.out.println(m.getName() + " - Remaining: " + m.getStockQuantity()));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}