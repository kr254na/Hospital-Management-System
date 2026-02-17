package controllers;

import config.DbConfig;
import models.Room;
import models.Ward;
import services.WardService;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class WardController {
    private final Scanner scanner = new Scanner(System.in);
    private final WardService wardService = new WardService();
    public void addWard() {
        System.out.println("\n--- Create New Ward ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Ward Name (e.g., ICU, Pediatric, General): ");
            String name = scanner.nextLine();
            System.out.print("Enter Ward Number/Code: ");
            String number = scanner.nextLine();
            Ward ward = new Ward(name, number);
            wardService.saveWard(ward, conn);

            System.out.println("Ward '" + name + "' registered successfully!");
        } catch (Exception e) {
            System.err.println("Failed to add ward: " + e.getMessage());
        }
    }

    public void viewAllWards() {
        System.out.println("\n--- Registered Hospital Wards ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);

            if (wards.isEmpty()) {
                System.out.println("No wards registered in the system.");
                return;
            }

            System.out.printf("%-5s | %-15s | %-10s \n", "S.No", "Ward Name", "Code");
            System.out.println("------------------------------------------------------------");
            for (int i = 0; i < wards.size(); i++) {
                Ward w = wards.get(i);
                System.out.printf("%-5d | %-15s | %-10s \n",
                        (i + 1), w.getWardName(), w.getWardNumber());
            }
        } catch (Exception e) {
            System.err.println("Error fetching wards: " + e.getMessage());
        }
    }

    public void updateWard() {
        System.out.println("\n--- Update Ward Details ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);
            if (wards.isEmpty()) {
                System.out.println("No wards available to update.");
                return;
            }

            for (int i = 0; i < wards.size(); i++) {
                System.out.println((i + 1) + ". " + wards.get(i).getWardName());
            }
            System.out.print("Select Ward to update: ");
            int choice = Integer.parseInt(scanner.nextLine());
            Ward selected = wards.get(choice - 1);

            System.out.print("Enter New Name (Current: " + selected.getWardName() + "): ");
            String newName = scanner.nextLine();
            System.out.print("Enter New Code (Current: " + selected.getWardNumber() + "): ");
            String newCode = scanner.nextLine();

            selected.setWardName(newName);
            selected.setWardNumber(newCode);

            wardService.updateWard(selected, conn);
            System.out.println("Ward updated successfully!");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void viewWardDetails() {
        System.out.println("\n--- Ward Detailed View ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);
            for (int i = 0; i < wards.size(); i++) {
                System.out.println((i + 1) + ". " + wards.get(i).getWardName());
            }
            System.out.print("Select Ward to inspect: ");
            Ward selected = wards.get(Integer.parseInt(scanner.nextLine()) - 1);
            System.out.println("\nDetails for Ward: " + selected.getWardName());
            System.out.println("Code: " + selected.getWardNumber());
            System.out.println("Status: Active");
            System.out.println("-----------------------------------");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void deleteWard() {
        System.out.println("\n--- Remove Ward ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);
            for (int i = 0; i < wards.size(); i++) {
                System.out.println((i + 1) + ". " + wards.get(i).getWardName() + " (" + wards.get(i).getWardNumber() + ")");
            }
            System.out.print("Select Ward to remove (number): ");
            int choice = Integer.parseInt(scanner.nextLine());
            Ward selected = wards.get(choice - 1);
            wardService.deleteWard(selected.getId(), conn);
            System.out.println("Ward deleted successfully.");
        } catch (Exception e) {
            System.err.println("Deletion failed: " + e.getMessage());
        }
    }
}