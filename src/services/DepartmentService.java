package services;

import exceptions.InvalidInputException;
import models.Department;
import repository.DepartmentJdbcRepository;
import repository.DoctorJdbcRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DepartmentService {
    private final DepartmentJdbcRepository departmentRepo = new DepartmentJdbcRepository();

    public List<Department> findAll(Connection conn) throws SQLException {
        return departmentRepo.findAll(conn);
    }

    public Optional<Department> findById(UUID id, Connection conn) throws SQLException {
        if (id == null) {
            return Optional.empty();
        }
        return departmentRepo.findById(id, conn);
    }

    public void updateDepartment(Department dept, Connection conn) throws SQLException, InvalidInputException {
        if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) {
            throw new InvalidInputException("Department name cannot be empty.");
        }
        if (dept.getHeadDoctorId() != null) {
            DoctorJdbcRepository docRepo = new DoctorJdbcRepository();
            if (docRepo.findById(dept.getHeadDoctorId(), conn).isEmpty()) {
                throw new InvalidInputException("Assigned HOD must be a valid registered doctor.");
            }
        }
        departmentRepo.update(dept, conn);
    }

    public void saveDepartment(Department department, Connection conn) throws SQLException {
        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        departmentRepo.save(department, conn);
    }

    public void deleteDepartment(UUID id, Connection conn) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Department ID cannot be null.");
        }
        try {
            departmentRepo.delete(id, conn);
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                throw new SQLException("Cannot delete department: Doctors are still assigned to it.");
            }
            throw e;
        }
    }
}