package com.medisys;

import javafx.beans.property.SimpleStringProperty;

public class Doctor {
    private final SimpleStringProperty name;
    private final SimpleStringProperty gmail;
    private final SimpleStringProperty faculty;
    private final SimpleStringProperty room;

    public Doctor(String name, String gmail, String faculty, String room) {
        this.name = new SimpleStringProperty(name);
        this.gmail = new SimpleStringProperty(gmail);
        this.faculty = new SimpleStringProperty(faculty);
        this.room = new SimpleStringProperty(room);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public String getGmail() {
        return gmail.get();
    }

    public void setGmail(String value) {
        gmail.set(value);
    }

    public String getFaculty() {
        return faculty.get();
    }

    public void setFaculty(String value) {
        faculty.set(value);
    }

    public String getRoom() {
        return room.get();
    }

    public void setRoom(String value) {
        room.set(value);
    }
}
