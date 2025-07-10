package com.medisys.model;

import javafx.beans.property.SimpleStringProperty;

public class UpcomingAppointment {
    private final SimpleStringProperty date;
    private final SimpleStringProperty room;
    private final SimpleStringProperty department;
    private final SimpleStringProperty doctor;
    private final SimpleStringProperty notes;

    public UpcomingAppointment(String date, String room, String department, String doctor, String notes) {
        this.date = new SimpleStringProperty(date);
        this.room = new SimpleStringProperty(room);
        this.department = new SimpleStringProperty(department);
        this.doctor = new SimpleStringProperty(doctor);
        this.notes = new SimpleStringProperty(notes);
    }

    public String getDate() { return date.get(); }
    public String getRoom() { return room.get(); }
    public String getDepartment() { return department.get(); }
    public String getDoctor() { return doctor.get(); }
    public String getNotes() { return notes.get(); }
}