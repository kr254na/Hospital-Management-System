package services;

import models.Ward;
import repository.WardJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WardService {
    private final WardJdbcRepository wardRepo = new WardJdbcRepository();

    public List<Ward> findAll(Connection conn) throws SQLException {
        return wardRepo.findAll(conn);
    }

    public Optional<Ward> findById(UUID id, Connection conn) throws SQLException {
        if (id == null) {
            return Optional.empty();
        }
        return wardRepo.findById(id, conn);
    }

    public void saveWard(Ward ward, Connection conn) throws SQLException {
        if (ward.getWardName() == null || ward.getWardName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ward name is required.");
        }
        wardRepo.save(ward, conn);
    }

    public void updateWard(Ward ward, Connection conn) throws SQLException {
        wardRepo.update(ward, conn);
    }

    public void deleteWard(UUID id, Connection conn) throws SQLException {
        wardRepo.delete(id, conn);
    }
}

