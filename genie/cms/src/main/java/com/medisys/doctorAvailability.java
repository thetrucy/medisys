package com.medisys;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//nhập dữ liệu bác sĩ
public class doctorAvailability {
    private static List<Doctor> Doctors;

    //đọc file doctor.txt
    public static void readFile(String[] args) {
        String fileName = "medisys\\Doctor.txt";
        BufferedReader reader = null;
        String docInfo = "";

        try {
            reader = new BufferedReader(new FileReader(fileName));
            while ((docInfo = reader.readLine()) != null) {
                if (docInfo.trim().isEmpty()) continue;

                //đọc file csv
                String[] tempInfo = docInfo.split(",");
                Doctor tempDoc = new Doctor(tempInfo[0].trim(), tempInfo[1].trim());

                //nhập vào list doctors
                Doctors.addDoctor(tempDoc);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
    }

    public void assignAppointment(String specialty) {
        
    }

    public void addDoctor(Doctor newDoc) {
        Doctors.add(newDoc);
    }

    public boolean timeCheckForDoctors(String time, Doctor doctor) {
        if (doctor.timeCheck(time)) {
            doctor.getAppointment(time);
            return true;
        }
        else return false;
    }
}
