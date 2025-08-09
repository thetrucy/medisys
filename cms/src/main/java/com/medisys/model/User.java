package com.medisys.model;

public abstract class User {
    // ID, name, phone, email
    protected int id; // Auto-incremented DB ID
    protected String nationalId; // For doctor_id or patient_id - string to handle large numbers
    protected String password;
    protected String name;
    protected String phone;
    protected boolean isDoctor; 

    public User(int id, String nationalId, String name, String phone) {
        this.id = id;
        this.nationalId = nationalId;
        this.name = name;
        this.phone = phone;
    }

    public User(String nationalId, String name, String phone) {
        this.nationalId = nationalId;
        this.name = name;
        this.phone = phone;
    }
    //for registry
    public User(String nationalId, String pwd, String name, String phone, boolean isDoctor) {
        this(nationalId, name, phone);
        password = pwd;
        this.isDoctor = isDoctor;
    }
        public User(int id, String nationalId, String pwd, String name, String phone, boolean isDoctor) {
        this(nationalId, name, phone);
        password = pwd;
        this.isDoctor = isDoctor;
    }
    // Getters
    public String getId() { return nationalId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public int getDbId() { return id; }
    public String getPassword() { return password; }
    public boolean isDoctor() { return isDoctor; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setID(int id) { this.id = id; } // setDbId

    public void setPassword(String password) {
        this.password = password;
    }
    // Polymorphic behavior
    public abstract String getUserType();  // e.g. "Doctor" or "Patient"
    public abstract String getSummary();   // Custom summary string
}
