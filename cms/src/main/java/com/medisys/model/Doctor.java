package com.medisys.model;

import javafx.beans.property.SimpleStringProperty;

public class Doctor {
    private long doctor_id;
    private int id; //---auto increment
    private final SimpleStringProperty name;
    private String phone;
    private final SimpleStringProperty email;
    private final SimpleStringProperty faculty;
    private final SimpleStringProperty room;



    public Doctor(long doctor_id,  String name, String faculty,  String phone, String email, String room) {
        this.doctor_id = doctor_id;
        this.phone = phone;
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.faculty = new SimpleStringProperty(faculty);
        this.room = new SimpleStringProperty(room);
    } 
    public Doctor(int id, long doctor_id, String name, String faculty, String phone, String email, String room) {
        this(doctor_id, name, faculty, phone, email, room);
        this.id = id;
    }
    //getters
    public long getId() {
        return doctor_id;
    }
    public String getName() {
        return name.get();
    }
    public String getPhone() {
        return phone;
    };
    public String getEmail() {
        return email.get();
    }
        public String getFaculty() {
        return faculty.get();
    }
    
    public String getRoom() {
        return room.get();
    }

    //setter
    public void setName(String value) {
        name.set(value);
    }
    public void setemail(String value) {
        email.set(value);
    }
    public void setFaculty(String value) {
        faculty.set(value);
    }
    
    public void setRoom(String value) {
        room.set(value);
    }
   
    public void setId(Integer value) {
        doctor_id = value;
    }
}