package services;

import exceptions.InvalidInputException;
import models.Nurse;
import repository.NurseJdbcRepository;
import utility.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NurseService {
    private final NurseJdbcRepository nurseRepo = new NurseJdbcRepository();

    public void saveNurse(Nurse nurse, Connection conn) throws SQLException, InvalidInputException {

        try {
            nurseRepo.saveNurse(nurse, conn);
        } catch (SQLException e) {
            throw new SQLException("Error in NurseService while saving: " + e.getMessage());
        }
    }

    public List<Nurse> findAll(Connection conn) throws SQLException {
        return nurseRepo.findAll(conn);
    }

    public List<Nurse> findByWardId(UUID wardId, Connection conn) throws SQLException {
        if (wardId == null) {
            throw new IllegalArgumentException("Ward ID cannot be null.");
        }
        return nurseRepo.findByWardId(wardId, conn);
    }

    public Optional<Nurse> findById(UUID id, Connection conn) throws SQLException {
        return nurseRepo.findById(id, conn);
    }

    public void updateWard(UUID nurseId, UUID newWardId, Connection conn) throws SQLException {
        if (nurseId == null || newWardId == null) {
            throw new IllegalArgumentException("IDs cannot be null for ward transfer.");
        }
        nurseRepo.updateWard(nurseId, newWardId, conn);
    }
}