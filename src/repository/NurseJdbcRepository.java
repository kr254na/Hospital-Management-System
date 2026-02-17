package repository;

import enums.Gender;
import models.Nurse;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NurseJdbcRepository {

    public void saveNurse(Nurse nurse, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);

            EmployeeJdbcRepository.save(nurse, conn);

            String sql = "INSERT INTO nurses (nurse_id, ward_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nurse.getId().toString());
                pstmt.setString(2, nurse.getWardId().toString());
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

    public Optional<Nurse> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN nurses n ON e.employee_id = n.nurse_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNurse(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Nurse> findByWardId(UUID wardId, Connection conn) throws SQLException {
        List<Nurse> nurses = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN nurses n ON e.employee_id = n.nurse_id " +
                "WHERE n.ward_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wardId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    nurses.add(mapResultSetToNurse(rs));
                }
            }
        }
        return nurses;
    }

    public void updateWard(UUID nurseId, UUID newWardId, Connection conn) throws SQLException {
        String sql = "UPDATE nurses SET ward_id = ? WHERE nurse_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newWardId.toString());
            pstmt.setString(2, nurseId.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Nurse> findAll(Connection conn) throws SQLException {
        List<Nurse> nurses = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN nurses n ON e.employee_id = n.nurse_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                nurses.add(mapResultSetToNurse(rs)); // Use your mapping helper here
            }
        }
        return nurses;
    }

    private Nurse mapResultSetToNurse(ResultSet rs) throws SQLException {
        try {
            Nurse nurse = new Nurse(
                    rs.getString("full_name"),
                    rs.getString("contact_info"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getDouble("salary"),
                    rs.getDate("date_of_joining").toLocalDate(),
                    UUID.fromString(rs.getString("ward_id"))
            );
            nurse.setUserId(UUID.fromString(rs.getString("nurse_id")));
            return nurse;
        } catch (Exception e) {
            throw new SQLException("Error mapping nurse data: " + e.getMessage());
        }
    }
}