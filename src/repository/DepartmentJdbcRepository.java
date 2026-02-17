package repository;

import models.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DepartmentJdbcRepository {

    public static void save(Department dept, Connection conn) throws SQLException {
        String sql = "INSERT INTO departments (dept_id, dept_name, head_doctor_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dept.getId().toString());
            pstmt.setString(2, dept.getDeptName());
            if (dept.getHeadDoctorId() != null) {
                pstmt.setString(3, dept.getHeadDoctorId().toString());
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            pstmt.executeUpdate();
        }
    }

    public Optional<Department> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM departments WHERE dept_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDepartment(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Department> findAll(Connection conn) throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY dept_name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        }
        return departments;
    }

    public void updateHOD(UUID deptId, UUID doctorId, Connection conn) throws SQLException {
        String sql = "UPDATE departments SET head_doctor_id = ? WHERE dept_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (doctorId != null) {
                pstmt.setString(1, doctorId.toString());
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }
            pstmt.setString(2, deptId.toString());
            pstmt.executeUpdate();
        }
    }



    public Optional<Department> findByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM departments WHERE dept_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDepartment(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void update(Department dept, Connection conn) throws SQLException {
        String sql = "UPDATE departments SET dept_name = ?, head_doctor_id = ? WHERE dept_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dept.getDeptName());
            if (dept.getHeadDoctorId() != null) {
                pstmt.setString(2, dept.getHeadDoctorId().toString());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }
            pstmt.setString(3, dept.getId().toString());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed: Department not found.");
            }
        }
    }

    public void delete(UUID id, Connection conn) throws SQLException {
        String sql = "DELETE FROM departments WHERE department_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deletion failed: Department not found.");
            }
        }
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        try{
            String headIdStr = rs.getString("head_doctor_id");
            UUID headId = (headIdStr != null) ? UUID.fromString(headIdStr) : null;
            Department dept = new Department(
                    rs.getString("dept_name"),
                    headId
            );
            dept.setDeptId(UUID.fromString(rs.getString("dept_id")));
            return dept;
        }
        catch(Exception e){
            throw new SQLException("Error mapping department data: " + e.getMessage());
        }
    }
}