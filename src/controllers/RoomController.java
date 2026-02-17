package controllers;

import config.DbConfig;
import models.Room;
import models.Ward;
import enums.RoomType;
import services.RoomService;
import services.WardService;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class RoomController {
    private final Scanner scanner = new Scanner(System.in);
    private final RoomService roomService = new RoomService();
    private final WardService wardService = new WardService();

    public void addRoom() {
        System.out.println("\n--- Add New Room ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Ward> wards = wardService.findAll(conn);
            if (wards.isEmpty()) {
                System.out.println("No wards found. Create a ward first!");
                return;
            }
            System.out.println("Select Ward for this Room:");
            for (int i = 0; i < wards.size(); i++) {
                System.out.println((i + 1) + ". " + wards.get(i).getWardName());
            }
            System.out.print("Choice: ");
            int wardChoice = Integer.parseInt(scanner.nextLine());
            Ward selectedWard = wards.get(wardChoice - 1);
            System.out.print("Room Number/Name (e.g., 101-A): ");
            String roomNo = scanner.nextLine();
            System.out.println("Select Room Type:");
            for (RoomType type : RoomType.values()) {
                System.out.println("- " + type.name());
            }
            System.out.print("Enter Type: ");
            RoomType type = RoomType.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Bed Capacity (Max Patients): ");
            int capacity = Integer.parseInt(scanner.nextLine());
            System.out.print("Price per Day: ");
            double price = Double.parseDouble(scanner.nextLine());
            Room room = new Room(selectedWard.getId(), roomNo, type, capacity, price);
            roomService.saveRoom(room, conn);
            System.out.println("Room " + roomNo + " added to " + selectedWard.getWardName() + "!");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid Room Type selected.");
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }

    public void viewRoomStatus() {
        System.out.println("\n--- Current Room Occupancy ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Room> rooms = roomService.findAll(conn);
            if (rooms.isEmpty()) {
                System.out.println("No rooms registered.");
                return;
            }
            System.out.printf("%-10s | %-12s | %-10s | %-10s | %-10s | %-10s | %-15s | %-5s\n",
                    "Room No", "Type", "Capacity", "Occupied", "Available","Ward No","Ward Name","Daily Rate");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            for (Room r : rooms) {
                int available = r.getTotalBeds() - r.getOccupiedBeds();
                System.out.printf("%-10s | %-12s | %-10d | %-10d | %-10d | %-10s | %-15s | %5.2f\n",
                        r.getRoomNumber(), r.getRoomType(), r.getTotalBeds(), r.getOccupiedBeds(), available,
                        wardService.findById(r.getWardId(),conn).get().getWardNumber(),
                        wardService.findById(r.getWardId(),conn).get().getWardName(),
                        r.getPricePerDay());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void updateRoomDetails() {
        System.out.println("\n--- Update Room Details ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Room> rooms = roomService.findAll(conn);
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println((i + 1) + ". " + rooms.get(i).getRoomNumber());
            }
            System.out.print("Select Room to update: ");
            Room selected = rooms.get(Integer.parseInt(scanner.nextLine()) - 1);

            System.out.print("New Price per Day (Current: " + selected.getPricePerDay() + "): ");
            double newPrice = Double.parseDouble(scanner.nextLine());

            System.out.print("New Bed Capacity (Current: " + selected.getTotalBeds() + "): ");
            int newCapacity = Integer.parseInt(scanner.nextLine());

            selected.setPricePerDay(newPrice);
            selected.setTotalBeds(newCapacity);

            roomService.updateRoom(selected, conn);
            System.out.println("Room details updated successfully!");
        } catch (Exception e) {
            System.err.println("Update failed: " + e.getMessage());
        }
    }

    public void viewAvailableRooms() {
        System.out.println("\n--- Rooms with Vacant Beds ---");
        try (Connection conn = DbConfig.getConnection()) {
            List<Room> available = roomService.findAvailableRooms(conn);
            if (available.isEmpty()) {
                System.out.println("All rooms are currently full.");
                return;
            }
            for (Room r : available) {
                System.out.printf("- Room %s (%s) | Empty Beds: %d\n",
                        r.getRoomNumber(), r.getRoomType(), (r.getTotalBeds() - r.getOccupiedBeds()));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void deleteRoom() {
        System.out.println("\n--- Delete Room ---");
        try (Connection conn = DbConfig.getConnection()) {
            System.out.print("Enter Room Number to delete: ");
            String roomNo = scanner.nextLine();
            roomService.deleteRoomByNumber(roomNo, conn);
            System.out.println("Room deleted successfully.");
        } catch (Exception e) {
            System.err.println("Deletion failed: " + e.getMessage());
        }
    }
}