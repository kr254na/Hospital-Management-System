package repository;

import enums.AppointmentStatus;
import exceptions.InvalidInputException;
import models.Appointment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AppointmentJdbcRepository {

    public static void save(Appointment appt, Connection conn) throws SQLException {
        String sql = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, " +
                "appointment_time, status, symptoms) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, appt.getId().toString());
            pstmt.setString(2, appt.getPatientId().toString());
            pstmt.setString(3, appt.getDoctorId().toString());
            pstmt.setTimestamp(4, Timestamp.valueOf(appt.getAppointmentTime()));
            pstmt.setString(5, appt.getStatus().name());
            pstmt.setString(6, appt.getSymptoms());
            pstmt.executeUpdate();
        }
    }

    public List<Appointment> findAllByStatus(String status, Connection conn) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.full_name FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.patient_id " +
                "WHERE a.status = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment app = new Appointment();
                    app.setAppointmentId(UUID.fromString(rs.getString("appointment_id")));
                    app.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
                    app.setPatientId(UUID.fromString(rs.getString("patient_id")));
                    app.setDoctorId(UUID.fromString(rs.getString("doctor_id")));
                    list.add(app);
                }
            } catch (InvalidInputException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    public List<Appointment> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_time DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment app = new Appointment();
                    app.setAppointmentId(UUID.fromString(rs.getString("appointment_id")));
                    app.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
                    String statusStr = rs.getString("status");
                    if (statusStr != null) {
                        app.setStatus(AppointmentStatus.valueOf(statusStr.toUpperCase()));
                    }
                    list.add(app);
                }
            } catch (InvalidInputException e) {
                throw new RuntimeException("Failed to fetch appointments");
            }
        }
        return list;
    }

    public Optional<Appointment> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToAppointment(rs));
            }
        }
        return Optional.empty();
    }

    public List<Appointment> findByDoctorId(UUID doctorId, Connection conn) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_time";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorId.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    public void updateStatus(UUID id, AppointmentStatus status, Connection conn) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setString(2, id.toString());
            pstmt.executeUpdate();
        }
    }

    public void delete(UUID id, Connection conn) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        }
    }


    public boolean hasConflict(UUID doctorId, LocalDateTime start, LocalDateTime end, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments " +
                "WHERE doctor_id = ? " +
                "AND appointment_time BETWEEN ? AND ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorId.toString());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(start));
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(end));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        try {
            Appointment appointment = new Appointment(
                    UUID.fromString(rs.getString("patient_id")),
                    UUID.fromString(rs.getString("doctor_id")),
                    rs.getTimestamp("appointment_time").toLocalDateTime(),
                    rs.getString("symptoms")
            );
            appointment.setAppointmentId(UUID.fromString(rs.getString("appointment_id")));
            return appointment;
        } catch (Exception e) {
            throw new SQLException("Error mapping appointment data: " + e.getMessage());
        }
    }
}