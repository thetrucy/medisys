package com.medisys.util;


import com.medisys.model.Appointment;
import com.medisys.model.Patient;
import com.medisys.model.Doctor;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static final String DATABASE_URL = "jdbc:sqlite:medical_cms.db"; // Local database file
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Establishes a connection to the SQLite database.
     * @return A Connection object.
     */
    private DatabaseManager() {
        createTables();
    }
    public static synchronized DatabaseManager getInstance() {
    if (instance == null) {
        instance = new DatabaseManager();
    }
    return instance;
    }
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Creates the patients and appointments tables if they don't exist.
     */
    public void createTables() {
        String createPatientsTableSQL = """
            CREATE TABLE IF NOT EXISTS patients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,   -- Database's internal ID
                username TEXT NOT NULL UNIQUE,            -- Your unique username
                password TEXT NOT NULL,                   -- Hashed password
                patient_id INTEGER NOT NULL UNIQUE,        -- Your unique 12-digit ID
                name TEXT,
                phone TEXT,
                dob TEXT
            );
            """;

        String createDoctorsTableSQL = """
            CREATE TABLE IF NOT EXISTS doctors (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                doctor_id INTEGER NOT NULL UNIQUE,
                name TEXT NOT NULL,
                faculty TEXT NOT NULL,
                phone TEXT NOT NULL UNIQUE,
                email TEXT UNIQUE,
                room TEXT
            );
            """;

        String createAppointmentsTableSQL = """
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                patient_id INTEGER NOT NULL,
                doctor_id INTEGER NOT NULL,
                field TEXT NOT NULL,
                appointment_time TEXT NOT NULL,
                doctor_name TEXT NOT NULL,
                patient_name TEXT NOT NULL,
                room TEXT NOT NULL,
                FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE RESTRICT
            );
            """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            // Create tables
            stmt.execute(createPatientsTableSQL);
            stmt.execute(createDoctorsTableSQL);
            stmt.execute(createAppointmentsTableSQL);
            System.out.println("Tables created successfully or already exist.");

            // Check if doctors table is empty and pre-populate
            if (isTableEmpty("doctors", conn)) {
                System.out.println("Doctors table is empty. Populating with initial data...");
                populateInitialDoctors(conn);
            }
            // Check if doctors table is empty and pre-populate
            if (isTableEmpty("patients", conn)) {
                System.out.println("Patients table is empty. Populating with initial data...");
                populateInitialPatients(conn);
            }
            if (isTableEmpty("appointments", conn)) {
                System.out.println("Appointments table is empty. Populating with initial data...");
                populateInitialAppointments(conn);
            }
            System.out.println("------Validate patients------");
            List<Patient> initialPatients = getAllPatients();
            for(Patient patient:initialPatients){
                System.out.println(patient.getUsername());
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables or populating initial data: " + e.getMessage());
        }
    }

    /**
     * Checks if a given table is empty.
     * @param tableName The name of the table to check.
     * @param conn The database connection.
     * @return true if the table is empty, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private boolean isTableEmpty(String tableName, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true; // Should not happen if table exists, but as a fallback
    }
    private void populateInitialPatients(Connection conn) {
        // You can use a hardcoded list of patients here
        List<Patient> initialPatients = new ArrayList<>();
        initialPatients.add(new Patient("apple","123123A@",79123123123L,"John Doe", "123-456-7890", "1990-01-01"));
        initialPatients.add(new Patient("banana","123123T@",7912341234L,"Jane Smith", "234-567-8901", "1985-05-15"));

        // SQL to insert patients
        String sql = "INSERT OR IGNORE INTO Patients(username, password, patient_id, name, phone, dob) VALUES(?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Patient patient : initialPatients) {
                pstmt.setString(1, patient.getUsername());
                pstmt.setString(2, patient.getPassword());
                pstmt.setLong(3, patient.getId());
                pstmt.setString(4, patient.getName());
                pstmt.setString(5, patient.getPhone());
                pstmt.setString(6, patient.getDOB());

                pstmt.executeUpdate();
            }
            System.out.println("Initial patients populated.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
        /**
     * Populates the doctors table with initial data.
     * This method is called only if the doctors table is empty.
     * @param conn The database connection.
     */
    private void populateInitialDoctors(Connection conn) {
        List<Doctor> initialDoctors = new ArrayList<>();
        // Use your Doctor constructor: (name, faculty, phone, email)
        initialDoctors.add(new Doctor(71111111,"Dr. John Smith", "General Practice", "0901234567", "john.smith@medisys.com", "Room 1"));
        initialDoctors.add(new Doctor(71111222,"Dr. Jane Doe", "Pediatrics", "0907654321", "jane.doe@medisys.com", "Room 2"));
        initialDoctors.add(new Doctor(71111333,"Dr. Robert Johnson", "Cardiology", "0912345678", "robert.j@medisys.com", "Room 3"));
        initialDoctors.add(new Doctor(72222111,"Dr. Mary Lee", "Dermatology", "0918765432", "mary.l@medisys.com", "Room 4"));
        initialDoctors.add(new Doctor(72222223,"Dr. David Kim", "Orthopedics", "0923456789", "david.k@medisys.com", "Room 5"));
        initialDoctors.add(new Doctor(71113222,"Dr. Sarah Chen", "Internal Medicine", "0929876543", "sarah.c@medisys.com", "Room 6"));

        // SQL should use 'faculty' as the column name in the DB
        String sql = "INSERT INTO doctors(doctor_id, name, faculty, phone, email, room) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (Doctor doctor : initialDoctors) {
                pstmt.setLong(1, doctor.getId());
                pstmt.setString(2, doctor.getName());
                pstmt.setString(3, doctor.getFaculty()); 
                pstmt.setString(4, doctor.getPhone());
                pstmt.setString(5, doctor.getEmail());
                pstmt.setString(6, doctor.getRoom());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            System.out.println("Initial doctors populated successfully.");
        } catch (SQLException e) {
            System.err.println("Error populating initial doctors: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rb_e) {
                System.err.println("Rollback failed: " + rb_e.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    public void populateInitialAppointments(Connection conn) {
        // 1. Get a demo Patient and Doctor from the database
        Patient patient = getPatientByUsername("apple"); 
        Doctor doctor = getDoctorById(71111111L); 

        if (patient == null || doctor == null) {
            System.err.println("Cannot populate appointments: demo patient or doctor not found.");
            return;
        }

        List<Appointment> initialAppointments = new ArrayList<>();
        
        // 2. Create the Appointment objects using the IDs from the fetched objects

        LocalDateTime appointmentTime1 = LocalDateTime.of(2025,07,12,15,0,0);
        LocalDateTime appointmentTime2 = LocalDateTime.of(2025,07,12,9,30,0);
        
        initialAppointments.add(new Appointment(
            -1, // Placeholder for the auto-incrementing ID
            patient.getId(), // Get patient's database ID
            doctor.getId(),  // Get doctor's database ID
            doctor.getFaculty(),
            appointmentTime1,
            doctor.getName(),
            patient.getName(),
            doctor.getRoom()
        ));
        initialAppointments.add(new Appointment(
        -1,
        patient.getId(),
        doctor.getId(),
        doctor.getFaculty(),
        appointmentTime2,
        doctor.getName(),
        patient.getName(),
        doctor.getRoom()
    ));
        // SQL should use 'faculty' as the column name in the DB
        String sql = "INSERT INTO appointments(patient_id, doctor_id, field, appointment_time, doctor_name, patient_name, room) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Start transaction

        for (Appointment appointment : initialAppointments) {
            pstmt.setLong(1, appointment.getPatientId());
            pstmt.setLong(2, appointment.getDoctorId());
            pstmt.setString(3, appointment.getField());
            pstmt.setString(4, appointment.getAppointmentTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            pstmt.setString(5, appointment.getDoctorName());
            pstmt.setString(6, appointment.getPatientName());
            pstmt.setString(7, appointment.getRoom());
            pstmt.addBatch();
        }
            pstmt.executeBatch();
            conn.commit();
            System.out.println("Initial appointments populated successfully.");
        } catch (SQLException e) {
            System.err.println("Error populating initial appointments: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rb_e) {
                System.err.println("Rollback failed: " + rb_e.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
    public int addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors(doctor_id, name, faculty, phone, email, room) VALUES(?,?,?,?,?,?)";
        int doctorId = -1;
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, doctor.getId());
                pstmt.setString(2, doctor.getName());
                pstmt.setString(3, doctor.getFaculty()); 
                pstmt.setString(4, doctor.getPhone());
                pstmt.setString(5, doctor.getEmail());
                pstmt.setString(6, doctor.getRoom());
                pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                doctorId = rs.getInt(1);
                System.out.println("Doctor added with ID: " + doctorId);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Error: A doctor with this phone or email already exists.");
            } else {
                System.err.println("Error adding doctor: " + e.getMessage());
            }
        }
        return doctorId;
    }
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, doctor_id, name, faculty, phone, email, room FROM doctors";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                    rs.getInt("id"),
                    rs.getLong("doctor_id"),
                    rs.getString("name"),
                    rs.getString("faculty"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("room")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving doctors: " + e.getMessage());
        }
        return doctors;
    }
      
    public Doctor getDoctorById(long doctorId) {
        String sql = "SELECT id, doctor_id, name, faculty, phone, email, room FROM doctors WHERE doctor_id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Doctor(
                    rs.getInt("id"),
                    rs.getLong("doctor_id"),
                    rs.getString("name"),
                    rs.getString("faculty"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("room")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving doctor by ID " + doctorId + ": " + e.getMessage());
        }
        return null;
    }
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, faculty = ?, phone = ?, email = ?, room = ? WHERE doctor_id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getFaculty());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setString(5, doctor.getRoom());
            pstmt.setLong(6, doctor.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor ID " + doctor.getId() + " updated successfully.");
                return true;
            } else {
                System.out.println("No doctor found with ID: " + doctor.getId() + " to update.");
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Error: Cannot update. Another doctor already has this phone or email.");
            } else {
                System.err.println("Error updating doctor: " + e.getMessage());
            }
            return false;
        }
    }
    public boolean deleteDoctor(long doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, doctorId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor ID " + doctorId + " deleted successfully.");
                return true;
            } else {
                System.out.println("No doctor found with ID: " + doctorId + " to delete.");
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("FOREIGN KEY constraint failed")) {
                System.err.println("Error: Cannot delete doctor ID " + doctorId + " because there are appointments associated with this doctor. Please reassign or delete their appointments first.");
            } else {
                System.err.println("Error deleting doctor: " + e.getMessage());
            }
            return false;
        }
    }
    public int addPatient(Patient patient) {
        String sql = "INSERT INTO patients(patient_id, name, phone, dob) VALUES(?,?,?,?)";
        int Id = -1;
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.setString(3, patient.getPhone());
            pstmt.setString(4, patient.getDOB());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                Id = rs.getInt(1);
                System.out.println("Patient added with database ID: " + Id);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: patients.phone")) {
                System.err.println("Error: A patient with this phone number already exists.");
            } else {
                System.err.println("Error adding patient: " + e.getMessage());
            }
        }
        return Id;
    }

    /**
     * Adds a new appointment to the database.
     * @param appointment The Appointment object to add.
     * @return The generated ID of the new appointment, or -1 if insertion failed.
     */
    public int addAppointment(Appointment appointments) { // MODIFIED SIGNATURE
        String sql = "INSERT INTO appointments(patient_id, doctor_id, field, appointment_time, doctor_name, patient_name, room) VALUES(?,?,?,?,?,?)"; // MODIFIED SQL
        int appointmentId = -1;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, appointments.getPatientId());
            pstmt.setLong(2, appointments.getDoctorId()); 
            pstmt.setString(3, appointments.getField());
            pstmt.setString(4, appointments.getAppointmentTime().format(DATE_TIME_FORMATTER));
            pstmt.setString(5, appointments.getDoctorName());
            pstmt.setString(6, appointments.getPatientName());
            pstmt.setString(7, appointments.getRoom());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                appointmentId = rs.getInt(1);
                System.out.println("Appointment added with ID: " + appointmentId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding appointment: " + e.getMessage());
        }
        return appointmentId;
    }
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Appointment with ID " + appointmentId + " deleted successfully");
                return true;
            } else {
                System.out.println("No appointment found with ID: " + appointmentId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all patients from the database.
     * @return A list of Patient objects.
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT username, patient_id, name, phone, dob FROM patients";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                patients.add(new Patient(
                    rs.getString("username"),
                    rs.getLong("patient_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("dob")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving patients: " + e.getMessage());
        }
        return patients;
    }
//get patient by username
    public Patient getPatientByUsername(String username) {
        String sql = "SELECT id, username, password, patient_id, name, phone, dob FROM Patients WHERE username = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Patient(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getLong("patient_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("dob")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving patient by username '" + username + "': " + e.getMessage());
        }
        return null;
    }
    /**
     * Retrieves all appointments from the database, including patient name.
     * @return A list of Appointment objects.
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT
                a.id,
                a.patient_id,
                p.name AS patient_name,
                a.doctor_id,        -- ADDED
                d.name AS doctor_name, -- ADDED
                a.field,
                a.appointment_time,
                a.doctor_name,
                a.patient_name, -- ADDED
                a.room
            FROM
                appointments a
            JOIN
                patients p ON a.patient_id = p.patient_id
            JOIN
                doctors d ON a.doctor_id = d.doctor_id -- JOIN WITH DOCTORS TABLE
            ORDER BY
                a.appointment_time
            """;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDateTime time = LocalDateTime.parse(rs.getString("appointment_time"), DATE_TIME_FORMATTER);
                appointments.add(new Appointment(
                    rs.getInt("id"),
                    rs.getLong("patient_id"),
                    rs.getString("field"),
                    time,
                    rs.getString("doctor_name"), // Display current doctor's name from DB
                    rs.getString("patient_name"),
                    rs.getString("room")
                    
                    // Note: If your Appointment model had a doctorId field, you'd retrieve it here:
                    //rs.getInt("doctor_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments: " + e.getMessage());
        }
        return appointments;
    }
    /**
     * Retrieves appointments for a specific patient ID.
     * @param patientId The ID of the patient.
     * @return A list of Appointment objects for the given patient.
     */
    public List<Appointment> getAppointmentsByPatient(long patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT
                a.id,
                a.patient_id,
                p.name AS patient_name,
                a.doctor_id,
                d.name AS doctor_name,
                a.field,
                a.appointment_time,
                a.doctor_name,
                a.patient_name,
                a.room
            FROM
                appointments a
            JOIN
                patients p ON a.patient_id = p.patient_id
            JOIN
                doctors d ON a.doctor_id = d.doctor_id
            WHERE
                a.patient_id = ?
            ORDER BY
                a.appointment_time
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDateTime time = LocalDateTime.parse(rs.getString("appointment_time"), DATE_TIME_FORMATTER);
                appointments.add(new Appointment(
                    rs.getInt("id"),
                    rs.getLong("patient_id"),
                    rs.getString("field"),
                    time,
                    rs.getString("doctor_name"), // Display current doctor's name from DB
                    rs.getString("patient_name"),
                    rs.getString("room")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments for patient ID " + patientId + ": " + e.getMessage());
        }
        return appointments;
    }

    /**
     * Retrieves appointments for a specific doctor ID.
     * @param doctorId The ID of the doctor.
     * @return A list of Appointment objects for the given doctor.
     */
    public List<Appointment> getAppointmentsByDoctor(long doctorId) { // NEW METHOD
        List<Appointment> appointments = new ArrayList<>();
        String sql = """
            SELECT
                a.id,
                a.patient_id,
                p.name AS patient_name,
                a.doctor_id,
                d.name AS doctor_name,
                a.field,
                a.appointment_time,
                a.doctor_name_at_booking,
                a.room
            FROM
                appointments a
            JOIN
                patients p ON a.patient_id = p.id
            JOIN
                doctors d ON a.doctor_id = d.id
            WHERE
                a.doctor_id = ?
            ORDER BY
                a.appointment_time
            """;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDateTime time = LocalDateTime.parse(rs.getString("appointment_time"), DATE_TIME_FORMATTER);
                appointments.add(new Appointment(
                    rs.getInt("id"),
                    rs.getLong("patient_id"),
                    rs.getString("field"),
                    time,
                    rs.getString("doctor_name"),
                    rs.getString("room"),
                    rs.getString("patient_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments for doctor ID " + doctorId + ": " + e.getMessage());
        }
        return appointments;
    }
    /**
     * Updates an existing patient's information.
     * @param patient The Patient object with updated information (ID must match an existing patient).
     * @return True if update was successful, false otherwise.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, phone = ?, dob = ? WHERE patient_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getPhone());
            pstmt.setString(3, patient.getDOB());
            pstmt.setLong(4, patient.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient ID " + patient.getId() + " updated successfully.");
                return true;
            } else {
                System.out.println("No patient found with ID: " + patient.getId() + " to update.");
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: patients.phone")) {
                System.err.println("Error: Cannot update. Another patient already has this phone number.");
            } else {
                System.err.println("Error updating patient: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Deletes a patient and all their associated appointments.
     * @param patientId The ID of the patient to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean deletePatient(long patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, patientId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient ID " + patientId + " and associated appointments deleted successfully.");
                return true;
            } else {
                System.out.println("No patient found with ID: " + patientId + " to delete.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("exports")
	public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
            return null;
        }
    }

}