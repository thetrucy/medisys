package com.medisys;

public class Patient {
    private String patientName;
    private String patientDOB;
    private String patientPhone;
    private String patientGender;
    private String guardianRela;
    private String guardianPhone;
    private String guardianName;

    public Patient(String name, String DOB, String phone, String gender) {
        this.patientName = name;
        this.patientDOB = DOB;
        this.patientPhone = phone;
        this.patientGender = gender;
    }

    public Patient(String name, String DOB, String phone, String gender, String guardRela, String guardPhone, String guardName) {
        this.patientName = name;
        this.patientDOB = DOB;
        this.patientPhone = phone;
        this.patientGender = gender;
        this.guardianName = guardName;
        this.guardianPhone = guardPhone;
        this.guardianRela = guardRela;
    }

    public String getName() {
        return this.patientName;
    }

    public String getDOB() {
        return this.patientDOB;
    }

    public String printPatientInfo(boolean isBookedForOther) {
        String patientInfo = "Thông tin bệnh nhân:" +
                "\nHọ và tên: " + this.patientName +
                "\nNgày tháng năm sinh: " + this.patientDOB +
                "\nSố điện thoại: " + this.patientPhone +
                "\nGiới tính: " + this.patientGender;

        if (isBookedForOther) {
            return patientInfo + "\nHọ tên người giám hộ: " + this.guardianName +
            "\nQuan hệ với bệnh nhân: " + this.guardianRela +
            "\nSố điện thoại người giám hộ: " + this.guardianPhone;
        }
        else return patientInfo;
    }
}
