package com.appointment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleMaker {
    private List<AppointmentMain> appointments;

    //construct a list to store appointments
    public ScheduleMaker() {
        this.appointments = new ArrayList<>();
    }

    //add new appointment to the list
    public boolean addAppointment(AppointmentMain appointment) {
        if (checkAppointment(appointment)) {
            appointments.add(appointment);
            return true;
        }
        return false;
    }
    
    //check if the appointment is valid
    public boolean checkAppointment(AppointmentMain appointment) {
        if (appointments.isEmpty()) {
            return true;
        }
        else {
            for (AppointmentMain tempAppointment : appointments) {
                if (tempAppointment.getTime() == appointment.getTime()) {
                    //print information of occupied appointment
                    //here if it's even needed
                    return false;
                }
            }
            return true;
        }
    }
}
