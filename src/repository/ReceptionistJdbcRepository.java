package repository;

import enums.DeskLocation;
import enums.Gender;
import models.Receptionist;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReceptionistJdbcRepository {

    public void saveReceptionist(Receptionist receptionist, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            EmployeeJdbcRepository.save(receptionist, conn);
            String sql = "INSERT INTO receptionists (receptionist_id, desk_location) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, receptionist.getId().toString());
                pstmt.setString(2, receptionist.getDeskLocation().name());
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Optional<Receptionist> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN receptionists r ON e.employee_id = r.receptionist_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReceptionist(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Receptionist> findAll(Connection conn) throws SQLException {
        List<Receptionist> receptionists = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN receptionists r ON e.employee_id = r.receptionist_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                receptionists.add(mapResultSetToReceptionist(rs));
            }
        }
        return receptionists;
    }

    public void updateDeskLocation(UUID receptionistId, DeskLocation newLocation, Connection conn) throws SQLException {
        String sql = "UPDATE receptionists SET desk_location = ? WHERE receptionist_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newLocation.name());
            pstmt.setString(2, receptionistId.toString());
            pstmt.executeUpdate();
        }
    }

    private Receptionist mapResultSetToReceptionist(ResultSet rs) throws SQLException {
        try {
            Receptionist receptionist = new Receptionist(
                    rs.getString("full_name"),
                    rs.getString("contact_info"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getDouble("salary"),
                    rs.getDate("date_of_joining").toLocalDate(),
                    DeskLocation.valueOf(rs.getString("desk_location"))
            );
            receptionist.setUserId(UUID.fromString(rs.getString("receptionist_id")));
            return receptionist;
        } catch (Exception e) {
            throw new SQLException("Error mapping receptionist data: " + e.getMessage());
        }
    }
}