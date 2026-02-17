package repository;

import models.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MedicineJdbcRepository {

    public static void save(Medicine medicine, Connection conn) throws SQLException {
        String sql = "INSERT INTO medicines (medicine_id, medicine_name, price_per_unit, stock_quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getId().toString());
            pstmt.setString(2, medicine.getName());
            pstmt.setDouble(3, medicine.getPricePerUnit());
            pstmt.setInt(4, medicine.getStockQuantity());
            pstmt.executeUpdate();
        }
    }

    public Optional<Medicine> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedicine(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Medicine> findByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE LOWER(medicine_name) = LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            List<Medicine> medicines = new ArrayList<>();
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    medicines.add(mapResultSetToMedicine(rs));
                }
                return medicines;
            }
        }
    }

    public boolean updateStock(UUID medicineId, int quantityChange, Connection conn) throws SQLException {
        String sql = "UPDATE medicines SET stock_quantity = stock_quantity + ? " +
                "WHERE medicine_id = ? AND (stock_quantity + ?) >= 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantityChange);
            pstmt.setString(2, medicineId.toString());
            pstmt.setInt(3, quantityChange);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Medicine> findLowStock(int threshold, Connection conn) throws SQLException {
        List<Medicine> lowStockList = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE stock_quantity <= ? ORDER BY stock_quantity ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, threshold);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lowStockList.add(mapResultSetToMedicine(rs));
                }
            }
        }
        return lowStockList;
    }

    public List<Medicine> findAll(Connection conn) throws SQLException {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medicines.add(mapResultSetToMedicine(rs));
            }
        }
        return medicines;
    }

    public void addStock(UUID id, int quantity, Connection conn) throws SQLException {
        String sql = "UPDATE medicines SET stock_quantity = stock_quantity + ? WHERE medicine_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, id.toString());
            pstmt.executeUpdate();
        }
    }

    private Medicine mapResultSetToMedicine(ResultSet rs) throws SQLException {
        try {
            Medicine medicine = new Medicine(
                    rs.getString("medicine_name"),
                    rs.getDouble("price_per_unit"),
                    rs.getInt("stock_quantity")
            );
            medicine.setMedicineId(UUID.fromString(rs.getString("medicine_id")));
            return medicine;
        } catch (Exception e) {
            throw new SQLException("Error mapping medicine data: " + e.getMessage());
        }
    }


}
