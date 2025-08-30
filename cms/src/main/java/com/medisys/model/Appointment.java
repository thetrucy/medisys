package com.medisys.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id; //appointment's id
    // private Doctor doctor;
    // private Patient patient;
    private String doctorId;
    private String patientId;
    private String field;
    private LocalDateTime appointmentTime;
    private String doctorName;
    private String patientName;
    private String room;

    // // Constructors
    // Constructor chính, đầy đủ thông tin
    public Appointment(int id, String doctorId, String patientId, String field, LocalDateTime appointmentTime, String doctorName, String patientName, String room) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.field = field;
        this.appointmentTime = appointmentTime;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.room = room;
    }

    // Getters
    public int getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getField() { return field; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public String getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getRoom() { return room; }
    public String getPatientName() { return patientName; }

    // Setters
    public void setID(Integer id) { this.id = id; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Appointment{" + "id=" + id + ", patientId=" + patientId + ", patientName='" + patientName + '\'' + ", doctorId=" + doctorId + ", doctorName='" + doctorName + '\'' + ", field='" + field + '\'' + ", appointmentTime=" + appointmentTime.format(formatter) + ", room='" + room + '\'' + '}';
    }
}