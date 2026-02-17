package models;

import enums.PaymentStatus;
import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;
import java.time.LocalDateTime;
import java.util.UUID;

public class Bill implements Identifiable {

    private final UUID billId;
    private final UUID patientId;
    private double consultationFee;
    private double roomCharges;
    private double totalAmount;
    private final LocalDateTime billingDate;
    private PaymentStatus status;

    public Bill(UUID patientId, double consultationFee,
                double roomCharges) throws InvalidInputException {

        ValidationUtil.validatePatient(patientId);
        ValidationUtil.validateAmount(consultationFee);
        ValidationUtil.validateAmount(roomCharges);
        this.billId = UUID.randomUUID();
        this.patientId = patientId;
        this.consultationFee = consultationFee;
        this.roomCharges = roomCharges;
        this.billingDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
        calculateTotal();
    }

    public UUID getBillId() {
        return billId;
    }


    public UUID getPatientId() {
        return patientId;
    }

    public double getConsultationFee() {
        return consultationFee;
    }


    public double getRoomCharges() {
        return roomCharges;
    }

    private void calculateTotal() {
        this.totalAmount = consultationFee + roomCharges;
    }

    @Override
    public UUID getId() {
        return billId;
    }

    public double getTotalAmount() { return totalAmount; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getBillingDate() { return billingDate; }

    public void setStatus(PaymentStatus status) throws InvalidInputException {
        ValidationUtil.validatePaymentStatus(status);
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "--- INVOICE [%s] ---\nDate: %s\nPatient ID: %s\n" +
                        "Consultation: ₹%.2f\nRoom: ₹%.2f\n" +
                        "TOTAL: ₹%.2f\nStatus: %s\n----------------------",
                billId.toString().substring(0, 8), billingDate, patientId.toString().substring(0, 8),
                consultationFee, roomCharges, totalAmount, status
        );
    }
}