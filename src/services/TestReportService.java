package services;

import enums.TestStatus;
import exceptions.InvalidInputException;
import models.TestReport;
import repository.TestReportJdbcRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestReportService {
    private final TestReportJdbcRepository testRepo = new TestReportJdbcRepository();

    public void saveReport(TestReport report, Connection conn) throws SQLException {
        if (report.getTestName() == null || report.getTestName().trim().isEmpty()) {
            throw new IllegalArgumentException("Test name cannot be empty.");
        }
        if (report.getTestCost() < 0) {
            throw new IllegalArgumentException("Test cost cannot be negative.");
        }
        testRepo.save(report, conn);
    }

    public List<TestReport> findPendingByPatient(UUID patientId, Connection conn) throws SQLException, InvalidInputException {
        if (patientId == null) {
            throw new InvalidInputException("Cannot search tests for a null Patient ID.");
        }
        if (conn == null || conn.isClosed()) {
            throw new SQLException("Database connection is closed or invalid.");
        }
        return testRepo.findPendingByPatient(patientId, conn);
    }

    public List<TestReport> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID is required.");
        }
        return testRepo.findByPatientId(patientId, conn);
    }

    public void updateReport(TestReport report, Connection conn) throws SQLException, InvalidInputException {
        if (report.getId() == null) {
            throw new InvalidInputException("Cannot update a report without a valid ID.");
        }
        if (report.getStatus() == TestStatus.COMPLETED &&
                (report.getResult() == null || report.getResult().trim().isEmpty())) {
            throw new InvalidInputException("A completed report must have clinical findings/results.");
        }
        testRepo.update(report, conn);
    }

    public Optional<TestReport> findById(UUID reportId, Connection conn) throws SQLException {
        if (reportId == null) {
            return Optional.empty();
        }
        return testRepo.findById(reportId, conn);
    }

}