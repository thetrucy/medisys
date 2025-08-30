package com.medisys.model;


public class Patient extends User{
    private String dob;
    private String gender; // ADDED
    private String guardianRela;
    private String guardianPhone;
    private String guardianName;

    // Constructor đầy đủ nhất, dùng để đọc từ DB (patient.txt)
    public Patient(int id, String nationalId, String password, String name, String phone, String dob, String gender, String guardRela, String guardName, String guardPhone) {
        super(nationalId, password, name, phone, false);
        this.id = id; // Set the DB auto-incremented ID
        this.dob = (dob != null) ? dob : "";
        this.gender = (gender != null) ? gender : "";
        this.guardianRela = (guardRela != null) ? guardRela : "";
        this.guardianName = (guardName != null) ? guardName : "";
        this.guardianPhone = (guardPhone != null) ? guardPhone : "";
    }
    
    // Constructor dùng khi đăng ký tài khoản mới (từ LoginAndRegisterController)
    private Patient(String nationalId, String password, String name, String phone, String gender, String dob) {
        super(nationalId, password, name, phone, false);
        this.dob = (dob != null) ? dob : "";
        this.gender = (gender != null) ? gender : "";
        this.guardianRela = "";
        this.guardianName = "";
        this.guardianPhone = "";
    }

    // // Constructor dùng trong form đặt lịch cho bản thân
    // public Patient(String name, String phone, String dob, String gender) {
    //     super(-1, "", name, phone);
    //     this.dob = dob;
    //     this.gender = gender;
    // }

    // Constructor dùng trong form đặt lịch cho người khác (từ BookApmController)
    private Patient(String nationalId, String name, String phone, String dob, String gender, boolean isForBooking) {
        super(nationalId, name, phone);
        this.dob = (dob != null) ? dob : "";
        this.gender = (gender != null) ? gender : "";
    }

    // --- STATIC FACTORY METHODS ---
    
    /**
     * Creates a new Patient instance for the registration process.
     */
    public static Patient createForRegistration(String nationalId, String password, String name, String phone, String gender, String dob) {
        return new Patient(nationalId, password, name, phone, gender, dob);
    }

    /**
     * Creates a new Patient instance when booking an appointment for someone else.
     */
    public static Patient createForOtherBooking(String nationalId, String name, String phone, String dob, String gender) {
        return new Patient(nationalId, name, phone, dob, gender, true);
    }

    // Getters for Patient-specific properties
    public String getGender() { return gender; }
    public String getGuardianName() { return guardianName; }
    public String getGuardPhone() { return guardianPhone; }
    public String getGuardianRela() { return guardianRela; }
    public String getDOB() { return dob; }

    // Setters for Patient-specific properties
    public void setGender(String gender) { this.gender = gender; }
    public void setDOB(String dob) { this.dob = dob; }
    public void setGuard(String guardRela, String guardName, String guardPhone) {
        this.guardianName = guardName;
        this.guardianPhone = guardPhone;
        this.guardianRela = guardRela;
    }

    // Overriding abstract methods from User
    @Override
    public String getUserType() {
        return "Patient";
    }

    @Override
    public String getSummary() {
        return "Patient: " + getName() + " | DOB: " + getDOB() + " | Phone: " + getPhone();
    }

    // For debugging/printing
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() + // national ID
                ", name='" + getName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                '}';
    }
}