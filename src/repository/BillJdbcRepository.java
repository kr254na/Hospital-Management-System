package repository;

import enums.PaymentStatus;
import models.Bill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BillJdbcRepository {
    public void save(Bill bill, Connection conn) throws SQLException {
        String sql = "INSERT INTO bills (bill_id, patient_id, consultation_fee, " +
                "room_charges, total_amount, billing_date, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bill.getId().toString());
            pstmt.setString(2, bill.getPatientId().toString());
            pstmt.setDouble(3, bill.getConsultationFee());
            pstmt.setDouble(4, bill.getRoomCharges());
            pstmt.setDouble(5, bill.getTotalAmount());
            pstmt.setTimestamp(6, Timestamp.valueOf(bill.getBillingDate()));
            pstmt.setString(7, bill.getStatus().name());
            pstmt.executeUpdate();
        }
    }

    public Optional<Bill> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Bill> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE patient_id = ? ORDER BY billing_date DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        }
        return bills;
    }

    public void updatePaymentStatus(UUID id, PaymentStatus status, Connection conn) throws SQLException {
        String sql = "UPDATE bills SET payment_status = ? WHERE bill_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setString(2, id.toString());
            pstmt.executeUpdate();
        }
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        try {
            Bill bill = new Bill(
                    UUID.fromString(rs.getString("patient_id")),
                    rs.getDouble("consultation_fee"),
                    rs.getDouble("room_charges")
            );
            return bill;
        } catch (Exception e) {
            throw new SQLException("Failed to reconstruct Bill: " + e.getMessage());
        }
    }
}