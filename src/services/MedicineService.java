package services;

import models.Medicine;
import repository.MedicineJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MedicineService {
    private final MedicineJdbcRepository medRepo = new MedicineJdbcRepository();

    public void saveMedicine(Medicine med, Connection conn) throws SQLException {
        medRepo.save(med, conn);
    }

    public List<Medicine> findAll(Connection conn) throws SQLException {
        return medRepo.findAll(conn);
    }

    public List<Medicine> searchByName(String name, Connection conn) throws SQLException {
        return medRepo.findByName(name, conn);
    }

    public List<Medicine> findLowStock(int threshold, Connection conn) throws SQLException {
        return medRepo.findLowStock(threshold, conn);
    }

    public void addStock(UUID id, int quantity, Connection conn) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive.");
        }
        medRepo.addStock(id, quantity, conn);
    }
}