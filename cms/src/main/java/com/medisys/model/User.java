package com.medisys.model;

public abstract class User {
    // ID, name, phone, email
    protected int id; // Auto-incremented DB ID
    protected long nationalId; // For doctor_id or patient_id
    protected String name;
    protected String phone;

    public User(int id, long nationalId, String name, String phone) {
        this.id = id;
        this.nationalId = nationalId;
        this.name = name;
        this.phone = phone;
    }

    public User(long nationalId, String name, String phone) {
        this.nationalId = nationalId;
        this.name = name;
        this.phone = phone;
    }

    // Getters
    public long getId() { return nationalId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public int getDbId() { return id; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setID(int id) { this.id = id; } // setDbId

    // Polymorphic behavior
    public abstract String getUserType();  // e.g. "Doctor" or "Patient"
    public abstract String getSummary();   // Custom summary string
}
