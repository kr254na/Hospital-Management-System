package repository;

import enums.TestStatus;
import exceptions.InvalidInputException;
import models.TestReport;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestReportJdbcRepository {

    public static void save(TestReport report, Connection conn) throws SQLException {
        String sql = "INSERT INTO test_reports (report_id, patient_id, doctor_id, test_name, " +
                "test_cost, test_status, result_data, report_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, report.getId().toString());
            pstmt.setString(2, report.getPatientId().toString());
            pstmt.setString(3, report.getDoctorId().toString());
            pstmt.setString(4, report.getTestName());
            pstmt.setDouble(5, report.getTestCost());
            pstmt.setString(6, report.getStatus().name());
            pstmt.setString(7,(report.getReportDate()!=null)?(report.getReportDate().toString()):null);
            if (report.getResult() != null) {
                pstmt.setString(8, report.getResult());
            } else {
                pstmt.setNull(8, Types.CLOB);
            }
            pstmt.executeUpdate();
        }
    }

    public Optional<TestReport> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM test_reports WHERE report_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReport(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<TestReport> findByPatientId(UUID patientId, Connection conn) throws SQLException {
        List<TestReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM test_reports WHERE patient_id = ? ORDER BY report_date DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }
        return reports;
    }

    public void update(TestReport report, Connection conn) throws SQLException {
        String sql = "UPDATE test_reports SET result_data = ?, test_status = ?, report_date = ? WHERE report_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, report.getResult());
            pstmt.setString(2, report.getStatus().name());
            pstmt.setString(3, LocalDateTime.now().toString());
            pstmt.setString(4, report.getId().toString());
            pstmt.executeUpdate();
        }
    }

    public List<TestReport> findPendingByPatient(UUID patientId, Connection conn) throws SQLException, InvalidInputException {
        List<TestReport> list = new ArrayList<>();
        String sql = "SELECT * FROM test_reports WHERE patient_id = ? AND test_status = 'REQUESTED'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patientId.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TestReport tr = new TestReport();
                    tr.setReportId(UUID.fromString(rs.getString("report_id")));
                    tr.setTestName(rs.getString("test_name"));
                    String statusStr = rs.getString("test_status");
                    if (statusStr != null) {
                        tr.setStatus(TestStatus.valueOf(statusStr.toUpperCase()));
                    }
                    list.add(tr);
                }
            }
        }
        return list;
    }

    private TestReport mapResultSetToReport(ResultSet rs) throws SQLException {
        try {
            TestReport report = new TestReport(
                    UUID.fromString(rs.getString("patient_id")),
                    UUID.fromString(rs.getString("doctor_id")),
                    rs.getString("test_name"),
                    rs.getDouble("test_cost")
            );
            if (rs.getString("result_data") != null) {
                report.setResult(rs.getString("result_data"));
            }
            if (rs.getTimestamp("report_date") != null) {
                report.setReportDate(rs.getTimestamp("report_date").toLocalDateTime());
            }
            return report;
        } catch (Exception e) {
            throw new SQLException("Error mapping test report data: " + e.getMessage());
        }
    }
}