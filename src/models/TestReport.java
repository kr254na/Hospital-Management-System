package models;

import enums.TestStatus;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestReport implements Identifiable {
    private UUID reportId;
    private UUID patientId;
    private UUID doctorId;
    private String testName;
    private String result;
    private double testCost;
    private TestStatus status;
    private LocalDateTime reportDate;

    public TestReport() {
    }

    public TestReport(UUID patientId, UUID doctorId, String testName, double testCost)
            throws InvalidInputException {
        ValidationUtil.validatePatient(patientId);
        ValidationUtil.validateDoctor(doctorId);
        ValidationUtil.validateTestName(testName);
        ValidationUtil.validateAmount(testCost);

        this.reportId = UUID.randomUUID();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.testName = testName;
        this.testCost = testCost;
        this.status = TestStatus.REQUESTED;
        this.reportDate = null;
    }

    @Override
    public UUID getId() {
        return reportId;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) throws InvalidInputException {
        ValidationUtil.validateTestName(testName);
        this.testName = testName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) throws InvalidInputException {
        ValidationUtil.validateTestResult(result);
        this.result = result;
        this.status = TestStatus.COMPLETED;
    }

    public double getTestCost() {
        return testCost;
    }

    public void setTestCost(double testCost) throws InvalidInputException {
        ValidationUtil.validateAmount(testCost);
        this.testCost = testCost;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) throws InvalidInputException {
        ValidationUtil.validateStatus(status);
        this.status = status;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void enterResult(String result) throws InvalidInputException {
        if (result == null || result.trim().isEmpty()) {
            throw new InvalidInputException("Test result cannot be empty.");
        }
        this.result = result;
        this.status = TestStatus.COMPLETED;
    }

    public void updateStatus(TestStatus newStatus) throws InvalidInputException {
        if (newStatus == null) {
            throw new InvalidInputException("Test status cannot be null.");
        }
        this.status = newStatus;
    }

    public boolean isFinalized() {
        return this.status == TestStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return String.format("Report [%s] | Test: %s | Status: %s | Cost: ₹%.2f",
                reportId.toString().substring(0, 8), testName, status, testCost);
    }
}