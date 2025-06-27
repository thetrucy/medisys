package com.medisys;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentMain {
    private String doctorName;
    private LocalDate Date;
    private LocalTime Time;
    private Patient patient;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    

    //constructor for a single appointment
    public AppointmentMain(String doctorName, LocalDateTime dateTime, Patient patient) {
        this.doctorName = doctorName;
        this.Date = dateTime.toLocalDate();
        this.Time = dateTime.toLocalTime();
        this.patient = patient;
    }

    //get representative doctor name
    public String getDoc() {
        return this.doctorName;
    }

    //get appointment time
    public LocalTime getTime() {
        return this.Time;
    }

    //get appointment date
    public LocalDate getDate() {
        return this.Date;
    }

    //print out appointment's information
    @Override
    public String toString() {
        String dateTime = Date.atTime(Time).format(formatter);
        return "Appointment: \n" + "Doctor: " + doctorName 
                + "\n Time: " + dateTime + "\n Patient: " + patient.getName()
                + "\t" + patient.getDOB();
    }
}
