package repository;

import enums.Gender;
import models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DoctorJdbcRepository {

    public void saveDoctor(Doctor doctor, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            EmployeeJdbcRepository.save(doctor, conn);
            String sql = "INSERT INTO doctors (doctor_id, specialization, dept_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, doctor.getId().toString());
                pstmt.setString(2, doctor.getSpecialization());
                pstmt.setString(3, doctor.getDeptId().toString());
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

    public Optional<Doctor> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN doctors d ON e.employee_id = d.doctor_id " +
                "WHERE u.user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDoctor(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Doctor> findByDepartment(UUID deptId, Connection conn) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN doctors d ON e.employee_id = d.doctor_id " +
                "WHERE d.dept_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deptId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        }
        return doctors;
    }

    public void updateSpecialization(UUID doctorId, String newSpec, Connection conn) throws SQLException {
        String sql = "UPDATE doctors SET specialization = ? WHERE doctor_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newSpec);
            pstmt.setString(2, doctorId.toString());
            pstmt.executeUpdate();
        }
    }

    public List<Doctor> findByDepartmentId(UUID deptId, Connection conn) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN doctors d ON e.employee_id = d.doctor_id " +
                "WHERE d.dept_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deptId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        }
        return doctors;
    }

    public List<Doctor> searchByName(String docSearch, Connection conn) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN doctors d ON e.employee_id = d.doctor_id " +
                "WHERE LOWER(u.full_name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + docSearch + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        }
        return doctors;
    }

    public List<Doctor> findAll(Connection conn) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "JOIN doctors d ON e.employee_id = d.doctor_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving all doctors: " + e.getMessage());
        }
        return doctors;
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        try {
            Doctor doctor = new Doctor(
                    rs.getString("full_name"),
                    rs.getString("specialization"),
                    rs.getString("contact_info"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    UUID.fromString(rs.getString("dept_id")),
                    rs.getDouble("salary"),
                    rs.getDate("date_of_joining").toLocalDate()
            );
            doctor.setUserId(UUID.fromString(rs.getString("user_id")));
            return doctor;
        } catch (Exception e) {
            throw new SQLException("Error mapping doctor data: " + e.getMessage());
        }
    }
}