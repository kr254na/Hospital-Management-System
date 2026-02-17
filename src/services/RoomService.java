package services;

import models.Room;
import repository.RoomJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoomService {
    private final RoomJdbcRepository roomRepo = new RoomJdbcRepository();

    public void saveRoom(Room room, Connection conn) throws SQLException {
        roomRepo.save(room, conn);
    }

    public List<Room> findAvailableRooms(Connection conn) throws SQLException {
        return roomRepo.findAvailableRooms(conn);
    }

    public boolean incrementOccupancy(UUID roomId, Connection conn) throws SQLException {
        return roomRepo.incrementOccupancy(roomId, conn);
    }

    public void decrementOccupancy(UUID roomId, Connection conn) throws SQLException {
        roomRepo.decrementOccupancy(roomId, conn);
    }

    public List<Room> findAll(Connection conn) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Database connection is invalid or closed.");
        }
        try {
            return roomRepo.findAll(conn);
        } catch (SQLException e) {
            System.err.println("Error in RoomService.findAll: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Room> findById(UUID id, Connection conn) throws SQLException {
        if (id == null) {
            return Optional.empty();
        }
        try {
            return roomRepo.findById(id, conn);
        } catch (SQLException e) {
            System.err.println("RoomService Error: Could not find room with ID " + id);
            throw e;
        }
    }

    public void updateRoom(Room room, Connection conn) throws SQLException {
        roomRepo.update(room, conn);
    }


    public void deleteRoomByNumber(String roomNo, Connection conn) throws SQLException {
        Room room = roomRepo.findByRoomNumber(roomNo, conn)
                .orElseThrow(() -> new SQLException("Room not found."));
        if (room.getOccupiedBeds() > 0) {
            throw new SQLException("Cannot delete room: " + room.getRoomNumber() + " is currently occupied.");
        }
        roomRepo.deleteByRoomNumber(roomNo, conn);
    }

}