package services;

import exceptions.InvalidInputException;
import models.MedicalRecord;
import repository.MedicalRecordJdbcRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MedicalRecordService {
    private final MedicalRecordJdbcRepository recordRepo = new MedicalRecordJdbcRepository();

    public void saveRecord(MedicalRecord record, Connection conn) throws SQLException, InvalidInputException {
        if (record.getDiagnosis() == null || record.getDiagnosis().trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty for a medical record.");
        }
        recordRepo.save(record, conn);
    }

    public List<MedicalRecord> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID is required to fetch history.");
        }
        return recordRepo.findByPatientId(patientId, conn);
    }

}