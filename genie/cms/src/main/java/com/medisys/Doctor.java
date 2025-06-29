package com.medisys;

import java.util.ArrayList;
import java.util.List;

public class Doctor {
    private String docName;
    private String docSpecialty;
    private List<String> occupiedTime;

    public Doctor(String name, String specialty) {
        this.docName = name;
        this.docSpecialty = specialty;
        this.occupiedTime = new ArrayList<>();
    }

    public boolean timeCheck(String time) {
        if (occupiedTime.isEmpty()) return true;
        for (String tempTime : occupiedTime) {
            if (tempTime == time) return false;
        }
        return true;
    }

    public void getAppointment(String time) {
        this.occupiedTime.add(time);
    }

    public String getDocinfo() {
        return docName + ", " + docSpecialty;
    }
}
