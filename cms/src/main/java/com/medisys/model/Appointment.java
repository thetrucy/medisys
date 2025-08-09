package com.medisys.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private int id; //appointment's id
    private Doctor doctor;
    private Patient patient;
    private LocalDateTime appointmentTime;

    // Constructors
    public Appointment(String doctorId, String patientId, LocalDateTime appointmentTime) {
        this.patient = new Patient(patientId, "", "", "");
        this.doctor = new Doctor(doctorId, "", "", "", "", "");
        this.appointmentTime = appointmentTime;
    }
    public Appointment(int id, String doctorId, String patientId, String field, LocalDateTime appointmentTime, String doctorName, String patientName, String room) {
        this.id = id;
        this.patient = new Patient(patientId, patientName, "", "");
        this.doctor = new Doctor(doctorId, doctorName, field, "", "", room);
        this.appointmentTime = appointmentTime;
    }

    public Appointment(String doctorId, String patientId, String field, LocalDateTime appointmentTime, String doctorName, String patientName, String room) {
        this.patient = new Patient(patientId, patientName, "", "");
        this.doctor = new Doctor(doctorId, doctorName, field, "", "", room);
        this.appointmentTime = appointmentTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPatientId() {
        return patient.getId();
    }

    public String getField() {
        return doctor.getFaculty();
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public String getDoctorId() {
        return doctor.getId();
    }

    public String getDoctorName() {
        return doctor.getName();
    }

    public String getRoom() {
        return doctor.getRoom();
    }

    public String getPatientName() {
        return patient.getName();
    }

    // Setters (if needed)

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    /* public void setRoom(String room) {
        this.room = room;
    } */

    // For debugging/printing
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Appointment{" +
               "id=" + id +
               ", patientId=" + patient.getId() +
               ", patientName='" + patient.getName() + '\'' +
               ", doctorId=" + doctor.getId() +
               ", doctorName='" + doctor.getName() + '\'' +
               ", field='" + doctor.getFaculty() + '\'' +
               ", appointmentTime=" + appointmentTime.format(formatter) +
               ", room='" + doctor.getRoom() + '\'' + // Include room in toString
               '}';
    }
}