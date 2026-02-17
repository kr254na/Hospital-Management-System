package repository;

import exceptions.InvalidInputException;
import models.MedicalRecord;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MedicalRecordJdbcRepository {

    public static void save(MedicalRecord record, Connection conn) throws SQLException, InvalidInputException {
        String sql = "INSERT INTO medical_records (record_id, patient_id, doctor_id, appointment_id, " +
                "diagnosis, treatment_plan, prescription, last_updated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getId().toString());
            pstmt.setString(2, record.getPatientId().toString());
            pstmt.setString(3, record.getDoctorId().toString());
            pstmt.setString(4, record.getAppointmentId().toString());
            pstmt.setString(5, record.getDiagnosis());
            pstmt.setString(6, record.getTreatmentPlan());
            pstmt.setString(7, record.getPrescription());
            pstmt.setTimestamp(8, Timestamp.valueOf(record.getLastUpdated()));
            pstmt.executeUpdate();
        }
    }

    public Optional<MedicalRecord> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM medical_records WHERE record_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRecord(rs));
                }
            }
        } catch (InvalidInputException e) {
            throw new SQLException("Data integrity error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<MedicalRecord> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        List<MedicalRecord> history = new ArrayList<>();
        String sql = "SELECT * FROM medical_records WHERE patient_id = ? ORDER BY last_updated DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        history.add(mapResultSetToRecord(rs));
                    } catch (InvalidInputException e) {
                        System.err.println("Skipping corrupted record: " + rs.getString("record_id"));
                    }
                }
            }
        }
        return history;
    }

    public void updateClinicalNotes(UUID recordId, String diagnosis, String treatment, Connection conn) throws SQLException {
        String sql = "UPDATE medical_records SET diagnosis = ?, treatment_plan = ?, last_updated = ? WHERE record_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, diagnosis);
            pstmt.setString(2, treatment);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, recordId.toString());
            pstmt.executeUpdate();
        }
    }


    private MedicalRecord mapResultSetToRecord(ResultSet rs) throws SQLException, InvalidInputException {
        return new MedicalRecord(
                UUID.fromString(rs.getString("patient_id")),
                UUID.fromString(rs.getString("doctor_id")),
                UUID.fromString(rs.getString("appointment_id")),
                rs.getString("diagnosis"),
                rs.getString("treatment_plan"),
                rs.getString("prescription")
        );
    }
}