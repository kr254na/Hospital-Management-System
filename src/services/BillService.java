package services;

import enums.PaymentStatus;
import models.Bill;
import models.Patient;
import models.Room;
import repository.BillJdbcRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BillService {
    private final RoomService roomService = new RoomService();
    private final PatientService patientService = new PatientService();
    private final BillJdbcRepository billRepo = new BillJdbcRepository();
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();

    public static final double BASE_CONSULTATION_FEE = 500.0;

    public double calculateTotalBill(Patient patient, long daysStayed, Connection conn) throws Exception {
        double total = BASE_CONSULTATION_FEE;

        if (patient.getAssignedRoomId() != null) {
            Room room = roomService.findById(patient.getAssignedRoomId(), conn)
                    .orElseThrow(() -> new Exception("Assigned room data missing."));

            total += (daysStayed * room.getPricePerDay());
        }
        return total;
    }

    public void printBill(Bill bill, String patientName, long days, double roomCharges) {
        String fileName = "Bill_" + bill.getBillId().toString().substring(0, 8) + ".txt";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("============================================================");
            writer.println("                CITY GENERAL HOSPITAL BILLING               ");
            writer.println("============================================================");
            writer.printf("Bill ID     : %s\n", bill.getBillId());
            writer.printf("Date        : %s\n", bill.getBillingDate().format(dtf));
            writer.printf("Patient Name: %s\n", patientName);
            writer.printf("Status      : %s\n", bill.getStatus());
            writer.println("------------------------------------------------------------");
            writer.println(String.format("%-40s", "DESCRIPTION"));
            writer.println("------------------------------------------------------------");
            writer.printf("Days Stayed         : %d\n",days);
            writer.printf("Rate of Room/day    : Rs %.2f\n",roomCharges);
            writer.println("------------------------------------------------------------");
            writer.println(String.format("%-40s %15s", "CHARGES", "AMOUNT (INR)"));
            writer.println("------------------------------------------------------------");
            writer.printf("%-40s %15.2f\n", "Consultation Charges", bill.getConsultationFee());
            writer.printf("%-40s %15.2f\n", "Room & Accommodation Charges", bill.getRoomCharges());
            writer.println("------------------------------------------------------------");
            writer.printf("%-40s %15.2f\n", "TOTAL PAYABLE AMOUNT", bill.getTotalAmount());
            writer.println("============================================================");
            writer.println("          Thank you for choosing City General Hospital         ");
            writer.println("            Please keep this copy for your records             ");
            writer.println("============================================================");
            System.out.println("Bill exported successfully to: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to save bill file: " + e.getMessage());
        }
    }

    public void processPaymentAndDischarge(UUID patientId, long daysStayed, Connection conn) throws Exception {
        try {
            conn.setAutoCommit(false);
            Patient patient = patientService.findById(patientId, conn)
                    .orElseThrow(() -> new Exception("Patient not found."));
            double roomCharges = 0;
            double dailyCharge=0;
            if (patient.getAssignedRoomId() != null) {
                Room room = roomService.findById(patient.getAssignedRoomId(), conn)
                        .orElseThrow(() -> new Exception("Room data missing."));
                roomCharges = daysStayed * room.getPricePerDay();
                dailyCharge = room.getPricePerDay();
            }
            double total = BASE_CONSULTATION_FEE + roomCharges ;
            Bill bill = new Bill(patientId, BASE_CONSULTATION_FEE, roomCharges);
            bill.setStatus(PaymentStatus.PAID);
            printBill(bill,patientService.findById(patientId,conn).get().getName(),daysStayed,dailyCharge);
            patientService.dischargePatient(patientId, conn);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}