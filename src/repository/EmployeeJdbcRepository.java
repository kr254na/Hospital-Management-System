package repository;

import enums.Role;
import exceptions.InvalidInputException;
import models.Doctor;
import models.Employee;
import models.Nurse;
import models.Receptionist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EmployeeJdbcRepository {

    public List<Employee> findAll(Connection conn) throws SQLException, InvalidInputException {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.role, e.salary FROM users u " +
                "JOIN employees e ON u.user_id = e.employee_id " +
                "WHERE u.role IN ('DOCTOR', 'NURSE', 'RECEPTIONIST')";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);
                UUID id = UUID.fromString(rs.getString("user_id"));
                String name = rs.getString("full_name");
                String username = rs.getString("username");
                double salary = rs.getDouble("salary");
                Employee emp;
                switch (role) {
                    case DOCTOR -> {
                        emp = new Doctor();
                    }
                    case NURSE -> {
                        emp = new Nurse();
                    }
                    case RECEPTIONIST -> {
                        emp = new Receptionist();
                    }
                    default -> throw new SQLException("Unknown employee role: " + roleStr);
                }
                emp.setUserId(id);
                emp.setName(name);
                emp.setUsername(username);
                emp.setRole(role);
                emp.setSalary(salary);
                employees.add(emp);
            }
        }
        return employees;
    }

    public static void save(Employee employee, Connection conn) throws SQLException {
        UserJdbcRepository.save(employee, conn);
        String sql = "INSERT INTO employees (employee_id, salary, date_of_joining) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getId().toString());
            pstmt.setDouble(2, employee.getSalary());
            pstmt.setDate(3, java.sql.Date.valueOf(employee.getDateOfJoining()));
            pstmt.executeUpdate();
        }
    }

    public static void updateSalary(UUID employeeId, double newSalary, Connection conn) throws SQLException {
        String sql = "UPDATE employees SET salary = ? WHERE employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newSalary);
            pstmt.setString(2, employeeId.toString());
            pstmt.executeUpdate();
        }
    }

    public static void deleteEmployee(UUID employeeId, Connection conn) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId.toString());
            pstmt.executeUpdate();
        }
    }

    public static boolean exists(UUID employeeId, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employeeId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}