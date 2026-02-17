package services;

import exceptions.InvalidInputException;
import models.Employee;
import repository.EmployeeJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class EmployeeService {
    private final EmployeeJdbcRepository employeeRepo = new EmployeeJdbcRepository();

    public List<Employee> findAll(Connection conn) throws SQLException, InvalidInputException {
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Database connection is invalid or closed.");
        }
        List<Employee> employees = employeeRepo.findAll(conn);
        return employees;
    }

    public void updateSalary(UUID employeeId, double newSalary, Connection conn) throws SQLException {
        if (newSalary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        try {
            employeeRepo.updateSalary(employeeId, newSalary, conn);
        } catch (SQLException e) {
            throw new SQLException("EmployeeService failed to update salary: " + e.getMessage());
        }
    }

    public void deleteEmployee(UUID employeeId, Connection conn) throws SQLException {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID is required for deletion.");
        }
        try {
            employeeRepo.deleteEmployee(employeeId, conn);
        } catch (SQLException e) {
            throw new SQLException("EmployeeService failed to delete employee: " + e.getMessage());
        }
    }
}