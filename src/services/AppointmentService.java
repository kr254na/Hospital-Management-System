package services;

import models.Appointment;
import repository.AppointmentJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AppointmentService {
    private final AppointmentJdbcRepository appointmentRepo = new AppointmentJdbcRepository();

    public void bookAppointment(Appointment appointment, Connection conn) throws SQLException {
        LocalDateTime startTime = appointment.getAppointmentTime();
        LocalDateTime windowStart = startTime.minusMinutes(29);
        LocalDateTime windowEnd = startTime.plusMinutes(29);
        boolean isBusy = appointmentRepo.hasConflict(
                appointment.getDoctorId(),
                windowStart,
                windowEnd,
                conn
        );
        if (isBusy) {
            throw new SQLException("The doctor already has an appointment scheduled within 30 minutes of this time.");
        }
        appointmentRepo.save(appointment, conn);
    }

    public List<Appointment> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        return appointmentRepo.findByPatientId(patientId, conn);
    }

    public List<Appointment> findActiveAppointments(Connection conn) throws SQLException {
        return appointmentRepo.findAllByStatus("SCHEDULED", conn);
    }

    public List<Appointment> getScheduleByDoctor(UUID doctorId, Connection conn) throws SQLException {
        return appointmentRepo.findByDoctorId(doctorId, conn);
    }

    public void cancelAppointment(UUID appointmentId, Connection conn) throws SQLException {
        appointmentRepo.delete(appointmentId, conn);
    }
}