package repository;

import models.Ward;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WardJdbcRepository {

    public static void save(Ward ward, Connection conn) throws SQLException {
        String sql = "INSERT INTO wards (ward_id, ward_name, ward_number) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ward.getId().toString());
            pstmt.setString(2, ward.getWardName());
            pstmt.setString(3, ward.getWardNumber());
            pstmt.executeUpdate();
        }
    }

    public Optional<Ward> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM wards WHERE ward_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWard(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Ward> findAll(Connection conn) throws SQLException {
        List<Ward> wards = new ArrayList<>();
        String sql = "SELECT * FROM wards ORDER BY ward_name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                wards.add(mapResultSetToWard(rs));
            }
        }
        return wards;
    }

    public Optional<Ward> findByNumber(String wardNumber, Connection conn) throws SQLException {
        String sql = "SELECT * FROM wards WHERE ward_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wardNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWard(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void update(Ward ward, Connection conn) throws SQLException {
        String sql = "UPDATE wards SET ward_name = ?, ward_number = ? WHERE ward_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ward.getWardName());
            pstmt.setString(2, ward.getWardNumber());
            pstmt.setString(3, ward.getId().toString());
            pstmt.executeUpdate();
        }
    }

    public void delete(UUID wardId, Connection conn) throws SQLException {
        String sql = "DELETE FROM wards WHERE ward_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wardId.toString());
            pstmt.executeUpdate();
        }
    }

    private static Ward mapResultSetToWard(ResultSet rs) throws SQLException {
        try {
            Ward ward = new Ward(
                    rs.getString("ward_name"),
                    rs.getString("ward_number")
            );
            ward.setWardId(UUID.fromString(rs.getString("ward_id")));
            return ward;
        } catch (Exception e) {
            throw new SQLException("Error mapping ward data: " + e.getMessage());
        }
    }
}