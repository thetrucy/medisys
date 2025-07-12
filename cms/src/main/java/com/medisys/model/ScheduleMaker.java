package com.medisys.model;
import com.medisys.util.DatabaseManager;
import java.util.ArrayList;
import java.util.List;

public class ScheduleMaker {
    private List<Appointment> appointments;
    private DatabaseManager dbManager;

    //construct a list to store appointments
    public ScheduleMaker() {
        this.appointments = new ArrayList<>();
        this.dbManager = DatabaseManager.getInstance();
        this.appointments = dbManager.getAllAppointments();
    }

    //add new appointment to the list
    public boolean addAppointment(Appointment appointment) {
        if (checkAppointment(appointment)) {
            // Add to database first
            int appointmentId = dbManager.addAppointment(appointment);
            
            // If database insertion was successful, add to local list
            if (appointmentId > 0) {
                // Update the appointment ID with the generated one from database
                appointment.setID(appointmentId);
                appointments.add(appointment);
                return true;
            }
        }
        return false;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public DatabaseManager getData() {
        return dbManager;
    }

    public boolean removeAppointment(Appointment appointment) {
        if(this.dbManager.deleteAppointment(appointment.getId())) {
            return true;
        }
        else {
            return false;
        }
    }
    
    //check if the appointment is valid
    public boolean checkAppointment(Appointment appointment) {
        if (appointments.isEmpty()) {
            return true;
        }
        else {
            for (Appointment tempAppointment : appointments) {
                if (tempAppointment.getAppointmentTime().equals(appointment.getAppointmentTime())) {
                    //print information of occupied appointment
                    //here if it's even needed
                    return false;
                }
            }
            return true;
        }
    }
}
