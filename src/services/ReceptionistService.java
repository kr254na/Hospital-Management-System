package services;

import enums.DeskLocation;
import exceptions.InvalidInputException;
import models.Receptionist;
import repository.ReceptionistJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReceptionistService {
    private final ReceptionistJdbcRepository receptionistRepo = new ReceptionistJdbcRepository();

    public void saveReceptionist(Receptionist receptionist, Connection conn) throws SQLException, InvalidInputException {
        try {
            receptionistRepo.saveReceptionist(receptionist, conn);
        } catch (SQLException e) {
            throw new SQLException("Error in ReceptionistService while saving: " + e.getMessage());
        }
    }

    public Optional<Receptionist> findById(UUID id, Connection conn) throws SQLException {
        return receptionistRepo.findById(id, conn);
    }

    public List<Receptionist> findAll(Connection conn) throws SQLException {
        return receptionistRepo.findAll(conn);
    }

    public void updateDeskLocation(UUID receptionistId, DeskLocation newLocation, Connection conn) throws SQLException {
        if (receptionistId == null || newLocation == null) {
            throw new IllegalArgumentException("Receptionist ID and Location must be provided.");
        }
        receptionistRepo.updateDeskLocation(receptionistId, newLocation, conn);
    }
}