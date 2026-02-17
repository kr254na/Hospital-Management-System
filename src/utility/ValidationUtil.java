package utility;

import enums.*;
import exceptions.InvalidInputException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

public class ValidationUtil {

    private static final int MIN_WORKING_AGE = 18;
    private static final LocalDate HOSPITAL_FOUNDING_DATE = LocalDate.of(1990, 1, 1);
    private static final String WARD_NUMBER_REGEX = "^[a-zA-Z0-9-]+$";
    private static final double MAX_ROOM_RATE = 100000.00; // Cap for a VIP/Luxury Suite
    private static final int MAX_BEDS_PER_ROOM = 20;
    private static final String ROOM_NUMBER_REGEX = "^[a-zA-Z0-9-]+$";
    private static final int MIN_CLINICAL_LENGTH = 3;
    private static final int MAX_CLINICAL_LENGTH = 1000;
    private static final double MAX_MEDICINE_PRICE = 50000.00;
    private static final int MAX_STOCK_LEVEL = 10000;

    public static void validateName(String name) throws InvalidInputException {
        if (name == null || !name.matches("[A-Za-z ]{3,}")) {
            throw new InvalidInputException("Invalid name! Name must be at least 3 letters and contain only alphabets.");
        }
    }

    public static void validateDisease(String disease) throws InvalidInputException {
        if (disease == null || disease.trim().length() < 3) {
            throw new InvalidInputException("Invalid disease! Must be at least 3 characters.");
        }
    }

    public static void validateAmount(double amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Invalid amount! Must be positive.");
        }
    }

    public static void validateContact(String contact) throws InvalidInputException{
        if (contact == null || !contact.matches("^\\d{10}$")) {
            throw new InvalidInputException("Invalid contact number!");
        }
    }

    public static void validateDob(LocalDate dob) throws InvalidInputException{
        if (dob == null || dob.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Invalid date of birth!");
        }
    }

    public static void validateGender(Gender gender) throws InvalidInputException{
        if (gender == null) {
            throw new InvalidInputException("Gender cannot be null!");
        }
    }

    public static void validateSpecialization(String specialization) throws InvalidInputException{
        if (specialization == null || specialization.trim().length() < 3) {
            throw new InvalidInputException("Invalid specialization!");
        }
    }

    public static void validatePatient(UUID patientId) throws InvalidInputException{
        if(patientId == null){
            throw new InvalidInputException("Patient cannot be null");
        }
    }

    public static void validateDoctor(UUID doctorId) throws InvalidInputException{
        if(doctorId == null){
            throw new InvalidInputException("Doctor cannot be null");
        }
    }

    public static void validateStatus(AppointmentStatus status) throws InvalidInputException {
        if(status == null){
            throw new InvalidInputException("Appointment Status cannot be null");
        }
    }

    public static void validateAppointment(UUID appointmentId) throws InvalidInputException{
        if(appointmentId==null){
            throw new InvalidInputException("Appointment cannot be null");
        }
    }

    public static void validatePaymentStatus(PaymentStatus paymentStatus) throws InvalidInputException{
        if(paymentStatus==null){
            throw new InvalidInputException("Payment Status cannot be null");
        }
    }

    public static void validatePassword(String password)
            throws InvalidInputException {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password cannot be null!");
        }
        if (password.length() < 8) {
            throw new InvalidInputException(
                    "Password must be at least 8 characters long!"
            );
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidInputException(
                    "Password must contain at least one uppercase letter!"
            );
        }
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidInputException(
                    "Password must contain at least one lowercase letter!"
            );
        }
        if (!password.matches(".*[0-9].*")) {
            throw new InvalidInputException(
                    "Password must contain at least one digit!"
            );
        }
        if (!password.matches(".*[@#$%^&+=!].*")) {
            throw new InvalidInputException(
                    "Password must contain at least one special character!"
            );
        }
    }
    public static void validateUsername(String username)
            throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty!");
        }
        if (username.length() < 5 || username.length() > 20) {
            throw new InvalidInputException(
                    "Username must be between 5 and 20 characters!"
            );
        }
        if (!username.matches("[A-Za-z][A-Za-z0-9_]*")) {
            throw new InvalidInputException(
                    "Username must start with a letter and contain only letters, digits, or underscore!"
            );
        }
    }

    public static void validateRole(Role role)
            throws InvalidInputException {
        if (role == null) {
            throw new InvalidInputException("User role cannot be null!");
        }
    }

    public static void validateWard(UUID wardId) throws InvalidInputException{
        if (wardId == null) {
            throw new InvalidInputException("Ward ID cannot be null");
        }
    }

    public static void validateDepartment(UUID deptId) throws InvalidInputException{
        if (deptId == null) {
            throw new InvalidInputException("Department ID cannot be null");
        }
    }

    public static void validateSalary(double salary) throws InvalidInputException {
        if (salary < 0) {
            throw new InvalidInputException("Salary cannot be negative.");
        }
    }

    public static void validateJoiningDate(LocalDate dob, LocalDate doj) throws InvalidInputException {
        LocalDate today = LocalDate.now();
        if (doj.isBefore(HOSPITAL_FOUNDING_DATE)) {
            throw new InvalidInputException("Joining date cannot be earlier than " + HOSPITAL_FOUNDING_DATE);
        }
        if (dob != null) {
            int ageAtJoining = Period.between(dob, doj).getYears();
            if (ageAtJoining < MIN_WORKING_AGE) {
                throw new InvalidInputException("Employee must be at least 18 years old at the time of joining. Age at joining: " + ageAtJoining);
            }
        }
    }

    public static void validateDeskLocation(DeskLocation deskLocation) throws InvalidInputException {
        if(deskLocation == null){
            throw new InvalidInputException("Desk Location cannot be null");
        }
    }

    public static void validateAppointmentTime(LocalDateTime time) throws InvalidInputException {
        if (time==null || time.isBefore(LocalDateTime.now())) {
            throw new InvalidInputException("Appointment cannot be scheduled in the past.");
        }
    }

    public static void validateDepartmentName(String deptName) throws InvalidInputException {
        if(deptName == null || deptName.trim().isEmpty()){
            throw new InvalidInputException("Department name cannot be null");
        }
        if (deptName.length() < 3 || deptName.length() > 50) {
            throw new InvalidInputException("Department name must be between 3 and 50 characters.");
        }
    }

    public static void validateWardName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Ward name cannot be empty.");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new InvalidInputException("Ward name must be between 3 and 50 characters.");
        }
    }

    public static void validateWardNumber(String number) throws InvalidInputException {
        if (number == null || !number.matches(WARD_NUMBER_REGEX)) {
            throw new InvalidInputException("Invalid Ward Number. Use alphanumeric characters and hyphens only.");
        }
    }

    public static void validateDiagnosis(String diagnosis) throws InvalidInputException {
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new InvalidInputException("Diagnosis cannot be empty. Clinical accuracy is required.");
        }
        if (diagnosis.length() < MIN_CLINICAL_LENGTH) {
            throw new InvalidInputException("Diagnosis is too short. Please provide a valid medical term.");
        }
        if (diagnosis.length() > MAX_CLINICAL_LENGTH) {
            throw new InvalidInputException("Diagnosis exceeds the maximum character limit (1000).");
        }
    }

    public static void validateTreatmentPlan(String treatmentPlan) throws InvalidInputException {
        if (treatmentPlan == null || treatmentPlan.trim().isEmpty()) {
            throw new InvalidInputException("Treatment plan cannot be empty. A patient must have a care strategy.");
        }
        if (treatmentPlan.length() > MAX_CLINICAL_LENGTH) {
            throw new InvalidInputException("Treatment plan exceeds the character limit.");
        }
    }

    public static void validatePrescription(String prescription) throws InvalidInputException {
        if (prescription == null || prescription.trim().isEmpty()) {
            throw new InvalidInputException("Prescription cannot be empty. Even if no medication is given, please note 'None' or 'Observation'.");
        }
        if (prescription.length() > MAX_CLINICAL_LENGTH) {
            throw new InvalidInputException("Prescription detail is too long.");
        }
    }

    public static void validatePrice(double pricePerUnit) throws InvalidInputException {
        if (pricePerUnit < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
        if (pricePerUnit == 0) {
            throw new InvalidInputException("Price cannot be zero. Every item must have a cost.");
        }
        if (pricePerUnit > MAX_MEDICINE_PRICE) {
            throw new InvalidInputException("Price exceeds the hospital's allowed limit for a single medicine unit.");
        }
    }

    public static void validateQuantity(int stockQuantity) throws InvalidInputException {
        if (stockQuantity < 0) {
            throw new InvalidInputException("Stock quantity cannot be negative.");
        }
        if (stockQuantity > MAX_STOCK_LEVEL) {
            throw new InvalidInputException("Stock quantity exceeds maximum warehouse capacity (" + MAX_STOCK_LEVEL + ").");
        }
    }

    public static void validateDailyRate(double rate) throws InvalidInputException {
        if (rate <= 0) {
            throw new InvalidInputException("Daily rate must be greater than zero. Hospital services are not free.");
        }
        if (rate > MAX_ROOM_RATE) {
            throw new InvalidInputException("Daily rate exceeds the hospital's maximum billing cap of Rs" + MAX_ROOM_RATE);
        }
    }

    public static void validateRoomNumber(String roomNumber) throws InvalidInputException {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new InvalidInputException("Room number cannot be empty.");
        }
        if (!roomNumber.matches(ROOM_NUMBER_REGEX)) {
            throw new InvalidInputException("Invalid Room Number format. Use alphanumeric characters and hyphens only.");
        }
        if (roomNumber.length() > 10) {
            throw new InvalidInputException("Room number is too long (max 10 characters).");
        }
    }

    public static void validateBedCount(int totalBeds, int occupiedBeds) throws InvalidInputException {
        if (totalBeds <= 0) {
            throw new InvalidInputException("A room must have at least one bed to be registered.");
        }
        if (totalBeds > MAX_BEDS_PER_ROOM) {
            throw new InvalidInputException("Room capacity cannot exceed " + MAX_BEDS_PER_ROOM + " beds.");
        }
        if (totalBeds < occupiedBeds) {
            throw new InvalidInputException("New capacity cannot be less than current occupancy (" + occupiedBeds + ").");
        }
    }

    public static void validateRoomType(RoomType roomType) throws InvalidInputException {
        if(roomType == null){
            throw new InvalidInputException("Room Type cannot be null");
        }
    }

    public static void validateTestName(String name) throws InvalidInputException {
        if (name == null || name.trim().length() < 2) {
            throw new InvalidInputException("Test name is too short or invalid.");
        }
    }

    public static void validateTestResult(String result) throws InvalidInputException {
        if (result == null || result.trim().length() < 2) {
            throw new InvalidInputException("Test name is too short or invalid.");
        }
    }

    public static void validateStatus(TestStatus status) throws InvalidInputException {
        if(status == null){
            throw new InvalidInputException("Test Status cannot be null");
        }
    }
}
