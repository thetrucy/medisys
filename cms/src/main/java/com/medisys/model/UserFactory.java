package com.medisys.model;
/*Factory design pattern */
public class UserFactory {
    public static User createUser(String userType, String nationalId, String password, String name, String phone, String... otherData) {
        switch (userType.toLowerCase()) {
            case "doctor":
                // Expects: [faculty, email, room]
                if (otherData.length >= 3) {
                    return new Doctor(nationalId, password, name, otherData[0], phone, otherData[1], otherData[2]);
                }
                break;
            case "patient":
                // Expects: [gender, dob]
                if (otherData.length >= 2) {
                    return Patient.createForRegistration(nationalId, password, name, phone, otherData[0], otherData[1]);
                }
                break;
            default:
                System.out.println("Unknown user type: " + userType);
                return null;
        }
        return null;
    }
}
