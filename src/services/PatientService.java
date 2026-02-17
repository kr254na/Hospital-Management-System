package services;

import models.Patient;
import repository.PatientJdbcRepository;
import repository.RoomJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PatientService {
    private final RoomService roomService = new RoomService();
    private final PatientJdbcRepository patientRepo = new PatientJdbcRepository();
    public void admitPatient(Patient patient, UUID roomId, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            if (roomId != null) {
                boolean bedAllocated = roomService.incrementOccupancy(roomId, conn);
                if (!bedAllocated) {
                    throw new SQLException("Admission failed: Selected room has reached maximum capacity.");
                }
            }
            patientRepo.save(patient, conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Transaction rolled back due to error: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Patient> findAll(Connection conn) throws SQLException {
        return patientRepo.findAll(conn);
    }

    public List<Patient> findByName(String name, Connection conn) throws SQLException {
        return patientRepo.findByName(name, conn);
    }

    public void dischargePatient(UUID patientId, Connection conn) throws SQLException {
        try {
            Patient patient = patientRepo.findById(patientId, conn)
                    .orElseThrow(() -> new SQLException("Patient not found."));
            if (patient.getAssignedRoomId() != null) {
                roomService.decrementOccupancy(patient.getAssignedRoomId(), conn);
            }
            patientRepo.delete(patientId, conn);
        } catch (SQLException e) {
            throw e;
        }
    }


    public void updateCondition(UUID patientId, String newSummary, Connection conn) throws SQLException {
        patientRepo.updateCondition(patientId, newSummary, conn);
    }

    public Optional<Patient> findById(UUID patientId, Connection conn) throws SQLException {
        if (patientId == null) {
            return Optional.empty();
        }
        Optional<Patient> patientOpt = patientRepo.findById(patientId, conn);
        if (patientOpt.isEmpty()) {
            System.out.println("LOG: No patient found with ID " + patientId);
        }
        return patientOpt;
    }

    public void transferRoom(UUID patientId, UUID newRoomId, Connection conn) throws SQLException {
        try {
            conn.setAutoCommit(false);
            Patient patient = patientRepo.findById(patientId, conn)
                    .orElseThrow(() -> new SQLException("Patient not found."));
            if (patient.getAssignedRoomId() != null) {
                roomService.decrementOccupancy(patient.getAssignedRoomId(), conn);
            }
            boolean bedAllocated = roomService.incrementOccupancy(newRoomId, conn);
            if (!bedAllocated) {
                throw new SQLException("Target room is full.");
            }
            patientRepo.updateRoom(patientId, newRoomId, conn);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
