package com.medisys;

import java.time.LocalDate;

public class User {
    private String id; // Unique ID, crucial for file storage
    private String name;
    private LocalDate dob; // Use LocalDate for date of birth
    private String gender;
    private String phoneNumber;

    // Constructor
    public User(String id, String name, LocalDate dob, String gender, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDob() { return dob; }
    public String getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }

    // Setters (if needed for modification)
    public void setName(String name) { this.name = name; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", DOB: " + dob + ", Gender: " + gender + ", Phone: " + phoneNumber;
    }

    /**
     * Converts the User object into a string format suitable for file storage.
     * Fields are delimited by '|'.
     * @return A pipe-delimited string representation of the User.
     */
    public String toFileString() {
        return String.join("|", id, name, dob.toString(), gender, phoneNumber);
    }

    /**
     * Creates a User object from a file-storable string.
     * @param fileString The pipe-delimited string from the file.
     * @return A User object, or null if the string format is invalid.
     */
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length == 5) {
            String id = parts[0];
            String name = parts[1];
            LocalDate dob = LocalDate.parse(parts[2]);
            String gender = parts[3];
            String phoneNumber = parts[4];
            return new User(id, name, dob, gender, phoneNumber);
        }
        return null;
    }
}
