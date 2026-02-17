package services;

import exceptions.InvalidInputException;
import models.Department;
import models.Doctor;
import repository.DepartmentJdbcRepository;
import repository.DoctorJdbcRepository;
import utility.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DoctorService {
    private final DoctorJdbcRepository doctorRepo = new DoctorJdbcRepository();
    private final DepartmentJdbcRepository deptRepo = new DepartmentJdbcRepository();
    public void saveDoctor(Doctor doctor, Connection conn) throws SQLException, InvalidInputException {
        try {
            doctorRepo.saveDoctor(doctor, conn);
        } catch (SQLException e) {
            throw new SQLException("Failed to save doctor: " + e.getMessage());
        }
    }

    public List<Doctor> findAll(Connection conn) throws SQLException {
        return doctorRepo.findAll(conn);
    }

    public List<Doctor> findByDepartment(UUID deptId, Connection conn) throws SQLException {
        return doctorRepo.findByDepartmentId(deptId, conn);
    }

    public Optional<Doctor> findById(UUID id, Connection conn) throws SQLException {
        return doctorRepo.findById(id, conn);
    }

    public void updateSpecialization(UUID docId, String newSpec, Connection conn) throws SQLException {
        if (newSpec == null || newSpec.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be empty");
        }
        doctorRepo.updateSpecialization(docId, newSpec, conn);
    }

    public List<Doctor> searchByName(String docSearch, Connection conn) throws SQLException {
        if (docSearch == null || docSearch.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return doctorRepo.searchByName(docSearch, conn);
    }

}