package repository;

import enums.RoomType;
import models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoomJdbcRepository {

    public static void save(Room room, Connection conn) throws SQLException {
        String sql = "INSERT INTO rooms (room_id, ward_id, room_number, room_type, total_beds, occupied_beds, price_per_day) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getId().toString());
            pstmt.setString(2, room.getWardId().toString());
            pstmt.setString(3, room.getRoomNumber());
            pstmt.setString(4, room.getRoomType().name());
            pstmt.setInt(5, room.getTotalBeds());
            pstmt.setInt(6, room.getOccupiedBeds());
            pstmt.setDouble(7,room.getPricePerDay());
            pstmt.executeUpdate();
        }
    }

    public Optional<Room> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Room> findAvailableRoomsInWard(UUID wardId, Connection conn) throws SQLException {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE ward_id = ? AND occupied_beds < total_beds";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wardId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(mapResultSetToRoom(rs));
                }
            }
        }
        return availableRooms;
    }

    public boolean updateOccupancy(UUID roomId, int change, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET occupied_beds = occupied_beds + ? " +
                "WHERE room_id = ? AND (occupied_beds + ?) <= total_beds " +
                "AND (occupied_beds + ?) >= 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, change);
            pstmt.setString(2, roomId.toString());
            pstmt.setInt(3, change);
            pstmt.setInt(4, change);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean incrementOccupancy(UUID roomId, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET occupied_beds = occupied_beds + 1 " +
                "WHERE room_id = ? AND occupied_beds < total_beds";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomId.toString());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Room> findAll(Connection conn) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    public List<Room> findAvailableRooms(Connection conn) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE occupied_beds < total_beds";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    public void decrementOccupancy(UUID roomId, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET occupied_beds = occupied_beds - 1 " +
                "WHERE room_id = ? AND occupied_beds > 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomId.toString());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Notice: No occupancy change for Room ID " + roomId);
            }
        }
    }

    public Optional<Room> findByRoomNumber(String roomNo, Connection conn) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoom(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void update(Room room, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, " +
                "total_beds = ?, price_per_day = ? WHERE room_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType().name());
            pstmt.setInt(3, room.getTotalBeds());
            pstmt.setDouble(4, room.getPricePerDay());
            pstmt.setString(5, room.getId().toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update failed: Room with ID " + room.getId() + " not found.");
            }
        }
    }

    public void deleteByRoomNumber(String roomNo, Connection conn) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNo);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deletion failed: Room number " + roomNo + " does not exist.");
            }
        }
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        try {
            Room room = new Room(
                    UUID.fromString(rs.getString("ward_id")),
                    rs.getString("room_number"),
                    RoomType.valueOf(rs.getString("room_type")),
                    rs.getInt("total_beds"),
                    rs.getInt("price_per_day")
            );
            room.setRoomId(UUID.fromString(rs.getString("room_id")));
            room.setOccupiedBeds(rs.getInt("occupied_beds"));
            return room;
        } catch (Exception e) {
            throw new SQLException("Error mapping room data: " + e.getMessage());
        }
    }

}