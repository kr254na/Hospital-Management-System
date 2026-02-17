package models;

import enums.RoomType;
import exceptions.IllegalRoomStateException;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;
import java.util.UUID;

public class Room implements Identifiable {
    private UUID roomId;
    private final UUID wardId;
    private String roomNumber;
    private RoomType roomType;
    private int totalBeds;
    private int occupiedBeds;
    private double pricePerDay;
    public Room(UUID wardId, String roomNumber, RoomType roomType,
                int totalBeds, double pricePerDay) throws InvalidInputException {
        ValidationUtil.validateWard(wardId);
        ValidationUtil.validateRoomNumber(roomNumber);
        ValidationUtil.validateRoomType(roomType);
        ValidationUtil.validateDailyRate(pricePerDay);
        ValidationUtil.validateBedCount(totalBeds,0);
        this.roomId = UUID.randomUUID();
        this.wardId = wardId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.totalBeds = totalBeds;
        this.occupiedBeds = 0;
        this.pricePerDay = pricePerDay;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(int pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setOccupiedBeds(int occupiedBeds) {
        this.occupiedBeds = occupiedBeds;
    }

    public boolean hasAvailableBeds() {
        return occupiedBeds < totalBeds;
    }

    public synchronized void occupyBed() throws IllegalRoomStateException {
        if (hasAvailableBeds()) {
            occupiedBeds++;
        }
        else{
            throw new IllegalRoomStateException("No beds available");
        }
    }

    public synchronized void vacateBed() throws IllegalRoomStateException {
        if (occupiedBeds > 0) {
            occupiedBeds--;
        }
        else{
            throw new IllegalRoomStateException("Room is already empty");
        }
    }

    public void setRoomNumber(String roomNumber) throws InvalidInputException {
        ValidationUtil.validateRoomNumber(roomNumber);
        this.roomNumber = roomNumber;
    }

    public void setRoomType(RoomType roomType) throws InvalidInputException {
        ValidationUtil.validateRoomType(roomType);
        this.roomType = roomType;
    }

    public void setPricePerDay(double pricePerDay) throws InvalidInputException {
        ValidationUtil.validateDailyRate(pricePerDay);
        this.pricePerDay = pricePerDay;
    }

    public void setTotalBeds(int totalBeds) throws InvalidInputException {
        ValidationUtil.validateBedCount(totalBeds,this.occupiedBeds);
        this.totalBeds = totalBeds;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public UUID getWardId() {
        return wardId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }


    public int getTotalBeds() {
        return totalBeds;
    }

    public int getOccupiedBeds() {
        return occupiedBeds;
    }

    @Override
    public String toString() {
        return String.format("Room %s [%s] | Beds: %d/%d | ₹%.2f/day",
                roomNumber, roomType, occupiedBeds, totalBeds, pricePerDay);
    }

    @Override
    public UUID getId() {
        return roomId;
    }
}