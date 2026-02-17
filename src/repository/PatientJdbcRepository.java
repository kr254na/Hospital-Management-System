package repository;

import enums.Gender;
import models.Patient;
import java.sql.*;
        import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PatientJdbcRepository {

    public void save(Patient patient, Connection conn) throws SQLException {
            String sql = "INSERT INTO patients (patient_id, full_name, contact_number, " +
                    "disease_summary, date_of_birth, gender, assigned_doctor_id, assigned_room_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, patient.getId().toString());
                pstmt.setString(2, patient.getName());
                pstmt.setString(3, patient.getContact());
                pstmt.setString(4, patient.getDisease());
                pstmt.setDate(5, Date.valueOf(patient.getDob()));
                pstmt.setString(6, patient.getGender().name());
                if (patient.getAssignedDoctorId() != null)
                    pstmt.setString(7, patient.getAssignedDoctorId().toString());
                else
                    pstmt.setNull(7, Types.VARCHAR);
                if (patient.getAssignedRoomId() != null)
                    pstmt.setString(8, patient.getAssignedRoomId().toString());
                else
                    pstmt.setNull(8, Types.VARCHAR);
                pstmt.executeUpdate();
            }
    }

    public void updateCondition(UUID id, String summary, Connection conn) throws SQLException {
        String sql = "UPDATE patients SET disease_summary = ? WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, summary);
            pstmt.setString(2, id.toString());
            pstmt.executeUpdate();
        }
    }

    public Optional<Patient> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPatient(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error retrieving patient: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Patient> findAll(Connection conn) throws SQLException {
        String sql = "SELECT * FROM patients";
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql)) {
                List<Patient> patients = new ArrayList<>();
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
                return patients;
            }
        } catch (Exception e) {
            throw new SQLException("Error retrieving patient: " + e.getMessage());
        }
    }

    public List<Patient> findByName(String name, Connection conn) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE LOWER(full_name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error searching patients: " + e.getMessage());
        }
        return patients;
    }

    public void updateAssignment(UUID patientId, UUID doctorId, UUID roomId, Connection conn) throws SQLException {
        String sql = "UPDATE patients SET assigned_doctor_id = ?, assigned_room_id = ? WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (doctorId != null) pstmt.setString(1, doctorId.toString());
            else pstmt.setNull(1, Types.VARCHAR);
            if (roomId != null) pstmt.setString(2, roomId.toString());
            else pstmt.setNull(2, Types.VARCHAR);
            pstmt.setString(3, patientId.toString());
            pstmt.executeUpdate();
        }
    }

    public void updateRoom(UUID patientId, UUID newRoomId, Connection conn) throws SQLException {
        String sql = "UPDATE patients SET assigned_room_id = ? WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (newRoomId != null) {
                pstmt.setString(1, newRoomId.toString());
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }
            pstmt.setString(2, patientId.toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update failed: Patient not found.");
            }
        }
    }

    public void delete(UUID id, Connection conn) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Deletion failed: No patient found with ID " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Error deleting patient: " + e.getMessage());
        }
    }


    private Patient mapResultSetToPatient(ResultSet rs) throws Exception {
        String docIdStr = rs.getString("assigned_doctor_id");
        UUID docId = (docIdStr != null) ? UUID.fromString(docIdStr) : null;
        Patient patient = new Patient(
                rs.getString("full_name"),
                rs.getString("contact_number"),
                rs.getString("disease_summary"),
                rs.getDate("date_of_birth").toLocalDate(),
                Gender.valueOf(rs.getString("gender")),
                docId, UUID.fromString(rs.getString("assigned_room_id"))
        );
        patient.setPatientId(UUID.fromString(rs.getString("patient_id")));
        return patient;
    }

}