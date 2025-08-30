package com.medisys.util;


import com.medisys.model.Appointment;
import com.medisys.model.Patient;
import com.medisys.model.User;
import com.medisys.model.Doctor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private static DatabaseManager instance;

    // private static final String APP_DATA_FOLDER_NAME = ".medisys"; // Thư mục ẩn để lưu data
    // private static final String USER_HOME = System.getProperty("user.home");
    // private static final String DATA_DIR = USER_HOME + File.separator + APP_DATA_FOLDER_NAME;
    private static final String DATA_DIR = "data";
    private static final String DATA_USERS = DATA_DIR + "/user.txt";
    private static final String DATA_PATIENTS = DATA_DIR + "/patient.txt";
    private static final String DATA_DOCTORS = DATA_DIR + "/doctor.txt";
    private static final String DATA_APPOINTMENTS = DATA_DIR + "/appointment.txt";
    private static final String DATA_ID_COUNTER = DATA_DIR + "/id_counter.txt";

    private static final String DELIMITER = "|";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*----------------------------ID--------------------- */
    private static AtomicInteger userIdCounter = new AtomicInteger(0);
    private static AtomicInteger patientIdCounter = new AtomicInteger(0);//-> thread-safe
    private static AtomicInteger doctorIdCounter = new AtomicInteger(0);
    private static AtomicInteger appointmentIdCounter = new AtomicInteger(0);

    /**
     * Initializes the file-based storage system.
     */
    private DatabaseManager() {
        initializeFileStorage();
        loadIdCounters();
        initializeData(); // Initialize with sample data if files are empty
    }
    public static synchronized DatabaseManager getInstance() {
    if (instance == null) {
        instance = new DatabaseManager();
    }
    return instance;
    }
    private void initializeFileStorage() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Create the 'data' directory
            System.out.println("Created data directory: " + DATA_DIR);
            // ADDED: This helps you find where the folder is created
            System.out.println("Data directory absolute path: " + dataDir.getAbsolutePath());
        }
        createFileIfNotExists(DATA_USERS);
        createFileIfNotExists(DATA_PATIENTS);
        createFileIfNotExists(DATA_DOCTORS);
        createFileIfNotExists(DATA_APPOINTMENTS);
        createFileIfNotExists(DATA_ID_COUNTER);
    }
    private void createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Created data file: " + filePath);
            } catch (IOException e) {
                System.err.println("Error creating file " + filePath + ": " + e.getMessage());
            }
        }
    }
    /**
     * Loads the last used IDs from the id_counters.txt file.
     * If the file is empty or corrupted, counters are initialized to 0.
     * Also checks existing data files to ensure counters are not lower than max existing IDs.
     */
    private void loadIdCounters() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_ID_COUNTER))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String type = parts[0].trim();
                    int lastId = Integer.parseInt(parts[1].trim());
                    switch (type) {
                        case "user": userIdCounter.set(lastId); break;
                        case "patient": patientIdCounter.set(lastId); break;
                        case "doctor": doctorIdCounter.set(lastId); break;
                        case "appointment": appointmentIdCounter.set(lastId); break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: Error loading ID counters or file empty. Initializing to 0. " + e.getMessage());
            // If file is empty or corrupted, counters remain 0 or are set to 0.
        }

        // Ensure counters are never lower than the max ID already in the data files
        userIdCounter.set(Math.max(userIdCounter.get(), getMaxIdFromFile(DATA_USERS, 0)));
        patientIdCounter.set(Math.max(patientIdCounter.get(), getMaxIdFromFile(DATA_PATIENTS, 0)));
        doctorIdCounter.set(Math.max(doctorIdCounter.get(), getMaxIdFromFile(DATA_DOCTORS, 0)));
        appointmentIdCounter.set(Math.max(appointmentIdCounter.get(), getMaxIdFromFile(DATA_APPOINTMENTS, 0)));
        
        saveIdCounters(); // Save the (potentially updated) ID counters
    }

    private int getMaxIdFromFile(String filePath, int idIndex) {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER); // Split by delimiter
                if (parts.length > idIndex) {
                    try {
                        int currentId = Integer.parseInt(parts[idIndex].trim());
                        if (currentId > maxId) {
                            maxId = currentId;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore lines with malformed IDs
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading max ID from " + filePath + ": " + e.getMessage());
        }
        return maxId;
    }
    /**
     * Saves ID counters to the id_counter.txt file.
     */
        private void saveIdCounters() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_ID_COUNTER))) {
            writer.write("user=" + userIdCounter.get());
            writer.newLine();
            writer.write("patient=" + patientIdCounter.get());
            writer.newLine();
            writer.write("doctor=" + doctorIdCounter.get());
            writer.newLine();
            writer.write("appointment=" + appointmentIdCounter.get());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving ID counters: " + e.getMessage());
        }
    }
    public void initializeData() {
        // Check if files are empty and pre-populate with initial data
        if (isFileEmpty(DATA_DOCTORS)) {
            System.out.println("Doctors file is empty. Populating with initial data...");
            populateInitialDoctorsToFile();
        }
        if (isFileEmpty(DATA_PATIENTS)) {
            System.out.println("Patients file is empty. Populating with initial data...");
            populateInitialPatientsToFile();
        }
        if (isFileEmpty(DATA_APPOINTMENTS)) {
            System.out.println("Appointments file is empty. Populating with initial data...");
            populateInitialAppointmentsToFile();
        }
        saveIdCounters();
        System.out.println("------Validate patients------");
        List<Patient> initialPatients = getAllPatients();
        for(Patient patient : initialPatients) {
            System.out.println(patient.getName());
        }
    }

    private boolean isFileEmpty(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine() == null;
        } catch (IOException e) {
            System.err.println("Error checking if file is empty: " + filePath + " - " + e.getMessage());
            return true; // Assume empty if can't read
        }
    }


    private void populateInitialPatientsToFile() {
        // You can use a hardcoded list of patients here
        List<Patient> initialPatients = new ArrayList<>();
        // initialPatients.add(new Patient("079123123123","123123123A","John Doe", "123-456-7890", "1990-01-01"));
        // initialPatients.add(new Patient("079123412345", "123123123T","Jane Smith", "234-567-8901", "1985-05-15"));

        // SMART CHANGE: Sử dụng factory method để tạo patient và setDOB thủ công
        // Cách này đảm bảo dữ liệu khởi tạo đầy đủ và đúng chuẩn
        Patient patient1 = Patient.createForRegistration("079123123123", "pass123A", "John Doe", "1234567890", "Nam", "1990-01-01");
        initialPatients.add(patient1);

        Patient patient2 = Patient.createForRegistration("079123412345", "pass123T", "Jane Smith", "2345678901", "Nữ", "1985-05-15");
        initialPatients.add(patient2);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_PATIENTS, true));
             BufferedWriter userWriter = new BufferedWriter(new FileWriter(DATA_USERS, true))) {
            for (Patient patient : initialPatients) {
                int newPatientId = patientIdCounter.incrementAndGet();
                int newUserId = userIdCounter.incrementAndGet();
                // Format: id|username|password|patient_id|name|phone|dob|gender|guardrela|guardname|guardphone
                String patientLine = String.join(DELIMITER,
                    String.valueOf(newPatientId),
                    String.valueOf(patient.getId()),
                    patient.getPassword(),
                    patient.getName(),
                    patient.getPhone(),
                    patient.getDOB(),
                    patient.getGender(),
                    patient.getGuardianRela(),
                    patient.getGuardianName(),
                    patient.getGuardPhone()
                );
                writer.write(patientLine);
                writer.newLine();

                String userLine = String.join(DELIMITER,
                String.valueOf(newUserId),
                patient.getId(),
                patient.getPassword(),
                patient.getName(),
                patient.getPhone(),
                "false" // isDoctor = false for patients
                );
                userWriter.write(userLine);
                userWriter.newLine();
            }
            saveIdCounters();
            System.out.println("Initial patients populated to file.");
        } catch (IOException e) {
            System.err.println("Error populating initial patients: " + e.getMessage());
        }
    }

    private void populateInitialDoctorsToFile() {
        List<Doctor> initialDoctors = new ArrayList<>();
        // Use your Doctor constructor: (doctor_id, name, faculty, phone, email, room)
        initialDoctors.add(new Doctor("079111111111","Dr. John Smith", "General Practice", "0901234567", "john.smith@medisys.com", "Room 1"));
        initialDoctors.add(new Doctor("079171111222","Dr. Jane Doe", "Pediatrics", "0907654321", "jane.doe@medisys.com", "Room 2"));
        initialDoctors.add(new Doctor("079171111333","Dr. Robert Johnson", "Cardiology", "0912345678", "robert.j@medisys.com", "Room 3"));
        initialDoctors.add(new Doctor("079172222111","Dr. Mary Lee", "Dermatology", "0918765432", "mary.l@medisys.com", "Room 4"));
        initialDoctors.add(new Doctor("079172222223","Dr. David Kim", "Orthopedics", "0923456789", "david.k@medisys.com", "Room 5"));
        initialDoctors.add(new Doctor("079171113222","Dr. Sarah Chen", "Internal Medicine", "0929876543", "sarah.c@medisys.com", "Room 6"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DOCTORS, true));
             BufferedWriter userWriter = new BufferedWriter(new FileWriter(DATA_USERS, true))) {
            for (Doctor doctor : initialDoctors) {
                int newDoctorId = doctorIdCounter.incrementAndGet();
                int newUserId = userIdCounter.incrementAndGet();
                // Format: id|doctor_id|name|faculty|phone|email|room
                String doctorLine = String.join(DELIMITER,
                    String.valueOf(newDoctorId),
                    String.valueOf(doctor.getId()),
                    doctor.getName(),
                    doctor.getFaculty(),
                    doctor.getPhone(),
                    doctor.getEmail(),
                    doctor.getRoom()
                );
                writer.write(doctorLine);
                writer.newLine();

                String userLine = String.join(DELIMITER,
                    String.valueOf(newUserId),
                    doctor.getId(),
                    "", // Password để trống cho bác sĩ (vì không có chức năng đăng nhập cho bác sĩ)
                    doctor.getName(),
                    doctor.getPhone(),
                    "true" // isDoctor = true for doctors
                );
                userWriter.write(userLine);
                userWriter.newLine();
            }
            saveIdCounters();
            System.out.println("Initial doctors populated to file successfully.");
        } catch (IOException e) {
            System.err.println("Error populating initial doctors: " + e.getMessage());
        }
    }

    private void populateInitialAppointmentsToFile() {
        // 1. Get a demo Patient and Doctor from the files

        Patient patient = getPatientByNationalID("079123123123");
        Doctor doctor = getDoctorById("079111111111");

        if (patient == null) {
            System.err.println("Cannot populate appointments: demo patient not found.");
        } if (doctor == null) {
            System.err.println("Cannot populate appointments: demo doctor not found.");
            return;
        }

        List<Appointment> initialAppointments = new ArrayList<>();
        
        // 2. Create the Appointment objects using the IDs from the fetched objects
        LocalDateTime appointmentTime1 = LocalDateTime.of(2025,8,25,15,0,0);
        LocalDateTime appointmentTime2 = LocalDateTime.of(2025,8,25,9,30,0);
        
        initialAppointments.add(new Appointment(
            -1, // Placeholder for the auto-incrementing ID
            doctor.getId(),  // Get doctor's ID
            patient.getId(), // Get patient's ID
            doctor.getFaculty(),
            appointmentTime1,
            doctor.getName(),
            patient.getName(),
            doctor.getRoom()
        ));
        initialAppointments.add(new Appointment(
            -1,
            doctor.getId(),
            patient.getId(),
            doctor.getFaculty(),
            appointmentTime2,
            doctor.getName(),
            patient.getName(),
            doctor.getRoom()
        ));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_APPOINTMENTS, true))) {
            for (Appointment appointment : initialAppointments) {
                int newId = appointmentIdCounter.incrementAndGet();
                // Format: id|doctor_id|patient_id|field|doctor_name|patient_name|appointment_time|room
                String appointmentLine = String.join(DELIMITER,
                    String.valueOf(newId),
                    String.valueOf(appointment.getDoctorId()),
                    String.valueOf(appointment.getPatientId()),
                    appointment.getField(),
                    appointment.getDoctorName(),
                    appointment.getPatientName(),
                    appointment.getAppointmentTime().format(DATE_TIME_FORMATTER),
                    appointment.getRoom()
                );
                writer.write(appointmentLine);
                writer.newLine();
            }
            saveIdCounters();
            System.out.println("Initial appointments populated to file successfully.");
        } catch (IOException e) {
            System.err.println("Error populating initial appointments: " + e.getMessage());
        }
    }
    public int addUser(User user) {
        int newId = userIdCounter.incrementAndGet();
        String userLine = String.join(DELIMITER,
            String.valueOf(newId),
            user.getId(),
            user.getPassword(),
            user.getName(),
            user.getPhone(),
            String.valueOf(user.isDoctor())
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_USERS, true))) {
            writer.write(userLine);
            writer.newLine();
            saveIdCounters();
            return newId;
        } catch (IOException e) {
            userIdCounter.decrementAndGet();
            return -1;
        }
    }

    public User getUserById(String nationalId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_USERS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 6 && parts[1].trim().equals(nationalId)) {
                    boolean isDoctor = Boolean.parseBoolean(parts[5].trim());
                    if (isDoctor) {
                        return getDoctorById(nationalId); 
                        // return getDoctorById(nationalId);
                    } else {
                        // return new Patient(
                        //     parts[1].trim(),
                        //     parts[2].trim(),
                        //     parts[3].trim(),
                        //     parts[4].trim(),
                        //     isDoctor
                        // );
                        return getPatientByNationalID(nationalId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file in getUserById: " + e.getMessage());
        }
        return null;
    }

    public User loginUser(String nationalId, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_USERS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1);
                if (parts.length >= 6 && parts[1].trim().equals(nationalId) && parts[2].trim().equals(password)) {
                    boolean isDoctor = Boolean.parseBoolean(parts[5].trim());
                    if (isDoctor) {
                        // Logic for doctor login (if needed in future)
                        return getDoctorById(nationalId); // Assuming you have a getDoctorById that returns a User
                    } else {
                        // **CRITICAL FIX**: Fetch the FULL patient details from patient.txt
                        Patient patient = getPatientByNationalID(nationalId);
                        System.out.println("DB id: " + patient.getDbId());
                        return getPatientByNationalID(nationalId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addDoctor(Doctor doctor) {
        int newId = doctorIdCounter.incrementAndGet(); // Get next ID
        // Format patient data into a single line, delimited by '|'
        String doctorLine = String.join(DELIMITER,
            String.valueOf(newId),
            String.valueOf(doctor.getId()),
            doctor.getName(),
            doctor.getFaculty(),
            doctor.getPhone(),
            doctor.getEmail(),
            doctor.getRoom()
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DOCTORS, true))) {
            writer.write(doctorLine);
            writer.newLine();
            saveIdCounters(); // Persist ID counter
            System.out.println("doctor added to file: " + doctor.getName() + " with ID: " + newId);
            return newId;
        } catch (IOException e) {
            System.err.println("Error adding doctor to file: " + e.getMessage());
            doctorIdCounter.decrementAndGet(); // Rollback ID if write fails
            return -1;
        }
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                // Ensure enough parts for a complete doctor record
                if (parts.length >= 7) {
                    try {
                        // Reconstruct Doctor object (id, doctor_id, name, faculty, phone, email, room)
                        doctors.add(new Doctor(
                            Integer.parseInt(parts[0].trim()),    // id
                            parts[1].trim(),      // doctor_id
                            parts[2].trim(),                      // name
                            parts[3].trim(),                      // faculty
                            parts[4].trim(),                      // phone
                            parts[5].trim(),                      // email
                            parts[6].trim()                       // room
                        ));
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Error parsing doctor data in file: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading doctors file: " + e.getMessage());
        }
        return doctors;
    }
      
    public Doctor getDoctorById(String doctorId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                // Ensure enough parts and doctor_id matches
                if (parts.length >= 7 && parts[1].trim().equals(String.valueOf(doctorId))) {
                    try {
                        // Reconstruct Doctor object (id, doctor_id, name, faculty, phone, email, room)
                        return new Doctor(
                            Integer.parseInt(parts[0].trim()),    // id
                            parts[1].trim(),      // doctor_id
                            parts[2].trim(),                      // name
                            parts[3].trim(),                      // faculty
                            parts[4].trim(),                      // phone
                            parts[5].trim(),                      // email
                            parts[6].trim()                       // room
                        );
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Error parsing doctor data in file: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error retrieving doctor by ID " + doctorId + ": " + e.getMessage());
        }
        return null;
    }
    /**
     * Updates an existing doctor's information using file-based I/O.
     * @param doctor The Doctor object with updated information (doctor_id must match an existing doctor).
     * @return True if update was successful, false otherwise.
     */
    public boolean updateDoctor(Doctor doctor) {
        List<String> lines = new ArrayList<>();
        boolean doctorFound = false;
        boolean phoneConflict = false;
        boolean emailConflict = false;
        
        // First, check for phone and email conflicts (excluding the doctor being updated)
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 7) {
                    String currentDoctorId = parts[1].trim();
                    String currentPhone = parts[4].trim();
                    String currentEmail = parts[5].trim();
                    // Check if another doctor has the same phone or email
                    if (currentDoctorId != doctor.getId()) {
                        if (currentPhone.equals(doctor.getPhone())) {
                            phoneConflict = true;
                            break;
                        }
                        if (currentEmail.equals(doctor.getEmail())) {
                            emailConflict = true;
                            break;
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error checking for conflicts: " + e.getMessage());
            return false;
        }
        
        if (phoneConflict || emailConflict) {
            System.err.println("Error: Cannot update. Another doctor already has this phone or email.");
            return false;
        }
        
        // Read all lines and update the matching doctor
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 7) {
                    String currentDoctorId = parts[1].trim();
                    if (currentDoctorId == doctor.getId()) {
                        // Update this doctor's information
                        // Format: id|doctor_id|name|faculty|phone|email|room
                        String updatedLine = String.join(DELIMITER,
                            parts[0].trim(),                    // id (unchanged)
                            parts[1].trim(),                    // doctor_id (unchanged)
                            doctor.getName(),                   // name (updated)
                            doctor.getFaculty(),                // faculty (updated)
                            doctor.getPhone(),                  // phone (updated)
                            doctor.getEmail(),                  // email (updated)
                            doctor.getRoom()                    // room (updated)
                        );
                        lines.add(updatedLine);
                        doctorFound = true;
                    } else {
                        lines.add(line); // Keep original line
                    }
                } else {
                    lines.add(line); // Keep malformed lines as-is
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading doctors file for update: " + e.getMessage());
            return false;
        }
        
        if (!doctorFound) {
            System.out.println("No doctor found with ID: " + doctor.getId() + " to update.");
            return false;
        }
        
        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DOCTORS))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Doctor ID " + doctor.getId() + " updated successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing updated doctors file: " + e.getMessage());
            return false;
        }
    }
    /**
     * Deletes a doctor using file-based I/O.
     * Checks for foreign key constraints (appointments associated with the doctor).
     * @param doctorId The ID of the doctor to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean deleteDoctor(String doctorId) {
        // First, check if there are any appointments associated with this doctor
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 8) {
                    String currentDoctorId = parts[1].trim(); // doctor_id is at index 1
                    if (currentDoctorId == doctorId) {
                        System.err.println("Error: Cannot delete doctor ID " + doctorId + " because there are appointments associated with this doctor. Please reassign or delete their appointments first.");
                        return false;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error checking for associated appointments: " + e.getMessage());
            return false;
        }
        
        // Now delete the doctor from the doctors file
        boolean doctorFound = false;
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 7) {
                    String currentDoctorId = parts[1].trim();
                    if (currentDoctorId == doctorId) {
                        doctorFound = true;
                        // Skip this line (delete the doctor)
                        continue;
                    }
                }
                lines.add(line); // Keep all other lines
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading doctors file for deletion: " + e.getMessage());
            return false;
        }
        
        if (!doctorFound) {
            System.out.println("No doctor found with ID: " + doctorId + " to delete.");
            return false;
        }
        
        // Write updated doctors file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DOCTORS))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Doctor ID " + doctorId + " deleted successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing updated doctors file: " + e.getMessage());
            return false;
        }
    }
    public int addPatient(Patient patient) {
        int newId = patientIdCounter.incrementAndGet(); // Get next ID
        // Format mới: id|nationalId|password|name|phone|dob|gender|guardianRela|guardianName|guardianPhone
        String patientLine = String.join(DELIMITER,
            String.valueOf(newId),
            patient.getId() != null ? patient.getId() : "",
            patient.getPassword() != null ? patient.getPassword() : "",
            patient.getName() != null ? patient.getName() : "",
            patient.getPhone() != null ? patient.getPhone() : "",
            patient.getDOB() != null ? patient.getDOB() : "",
            patient.getGender() != null ? patient.getGender() : "",
            patient.getGuardianRela() != null ? patient.getGuardianRela() : "",
            patient.getGuardianName() != null ? patient.getGuardianName() : "",
            patient.getGuardPhone() != null ? patient.getGuardPhone() : ""
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_PATIENTS, true))) {
            writer.write(patientLine);
            writer.newLine();
            saveIdCounters(); // Persist ID counter
            System.out.println("Patient added to file: " + patient.getName() + " with ID: " + newId);
            return newId;
        } catch (IOException e) {
            System.err.println("Error adding patient to file: " + e.getMessage());
            patientIdCounter.decrementAndGet(); // Rollback ID if write fails
            return -1;
        }
    }

    /**
     * Adds a new appointment to the database.
     * @param appointment The Appointment object to add.
     * @return The generated ID of the new appointment, or -1 if insertion failed.
     */
    public int addAppointment(Appointment appointment) { // MODIFIED SIGNATURE
        int newId = appointmentIdCounter.incrementAndGet(); // Get next ID
        // Format appointment data into a single line, delimited by '|'
        // Format: id|doctor_id|patient_id|field|doctor_name|patient_name|appointment_time|room
        String appointmentLine = String.join(DELIMITER,
            String.valueOf(newId),                                              // id
            String.valueOf(appointment.getDoctorId()),                          // doctor_id
            String.valueOf(appointment.getPatientId()),                         // patient_id
            appointment.getField(),                                             // field
            appointment.getDoctorName(),                                        // doctor_name
            appointment.getPatientName(),                                       // patient_name
            appointment.getAppointmentTime().format(DATE_TIME_FORMATTER),      // appointment_time
            appointment.getRoom()                                               // room
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_APPOINTMENTS, true))) {
            writer.write(appointmentLine);
            writer.newLine();
            saveIdCounters(); // Persist ID counter
            System.out.println("appointment added to file: with ID: " + newId);
            return newId;
        } catch (IOException e) {
            System.err.println("Error adding appointment to file: " + e.getMessage());
            appointmentIdCounter.decrementAndGet(); // Rollback ID if write fails
            return -1;
        }
    }
    /**
     * Deletes an appointment using file-based I/O.
     * @param appointmentId The ID of the appointment to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean deleteAppointment(int appointmentId) {
        boolean appointmentFound = false;
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 8) {
                    int currentAppointmentId = Integer.parseInt(parts[0].trim());
                    if (currentAppointmentId == appointmentId) {
                        appointmentFound = true;
                        // Skip this line (delete the appointment)
                        continue;
                    }
                }
                lines.add(line); // Keep all other lines
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading appointments file for deletion: " + e.getMessage());
            return false;
        }
        
        if (!appointmentFound) {
            System.out.println("No appointment found with ID: " + appointmentId);
            return false;
        }
        
        // Write updated appointments file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_APPOINTMENTS))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Appointment with ID " + appointmentId + " deleted successfully");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing updated appointments file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing appointment's information using file-based I/O.
     * @param appointment The Appointment object with updated information (id must match an existing appointment).
     * @return True if update was successful, false otherwise.
     */
    public boolean updateAppointment(Appointment appointment) {
        List<String> lines = new ArrayList<>();
        boolean appointmentFound = false;
        
        // Read all lines and update the matching appointment
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 8) {
                    int currentAppointmentId = Integer.parseInt(parts[0].trim());
                    if (currentAppointmentId == appointment.getId()) {
                        // Update this appointment's information
                        // Format: id|doctor_id|patient_id|field|doctor_name|patient_name|appointment_time|room
                        String updatedLine = String.join(DELIMITER,
                            parts[0].trim(),                                                    // id (unchanged)
                            String.valueOf(appointment.getDoctorId()),                          // doctor_id (updated)
                            String.valueOf(appointment.getPatientId()),                         // patient_id (updated)
                            appointment.getField(),                                             // field (updated)
                            appointment.getDoctorName(),                                        // doctor_name (updated)
                            appointment.getPatientName(),                                       // patient_name (updated)
                            appointment.getAppointmentTime().format(DATE_TIME_FORMATTER),      // appointment_time (updated)
                            appointment.getRoom()                                               // room (updated)
                        );
                        lines.add(updatedLine);
                        appointmentFound = true;
                    } else {
                        lines.add(line); // Keep original line
                    }
                } else {
                    lines.add(line); // Keep malformed lines as-is
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading appointments file for update: " + e.getMessage());
            return false;
        }
        
        if (!appointmentFound) {
            System.out.println("No appointment found with ID: " + appointment.getId() + " to update.");
            return false;
        }
        
        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_APPOINTMENTS))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Appointment ID " + appointment.getId() + " updated successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing updated appointments file: " + e.getMessage());
            return false;
        }
    }

    private Patient parsePatient(String[] parts) {
        // Đảm bảo có ít nhất 7 trường cơ bản
        if (parts.length < 7) {
            System.err.println("Lỗi parse dữ liệu bệnh nhân: Số lượng trường không đủ (tối thiểu 7): " + String.join(DELIMITER, parts));
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            String nationalId = parts[1].trim();
            String password = parts[2].trim();
            String name = parts[3].trim();
            String phone = parts[4].trim();
            String dob = parts[5].trim();
            String gender = parts[6].trim();

            // Khởi tạo các trường guardian mặc định là rỗng
            String guardianRela = "";
            String guardianName = "";
            String guardianPhone = "";

            // Nếu có nhiều hơn 7 trường, tức là có thông tin người giám hộ
            if (parts.length > 7) {
                // Kiểm tra và gán các trường nếu chúng tồn tại
                guardianRela = parts.length > 7 ? parts[7].trim() : "";
                guardianName = parts.length > 8 ? parts[8].trim() : "";
                guardianPhone = parts.length > 9 ? parts[9].trim() : "";
            }
            
            // Trả về một đối tượng Patient hoàn chỉnh với 10 tham số
            return new Patient(
                id,
                nationalId,
                password,
                name,
                phone,
                dob,
                gender,
                guardianRela,
                guardianName,
                guardianPhone
            );
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse patient ID: " + parts[0]);
            return null;
        }
    }

    /**
     * Retrieves all patients from the database.
     * @return A list of Patient objects.
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATIENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1); // Use -1 to keep trailing empty strings
                Patient patient = parsePatient(parts);
                if (patient != null) {
                    patients.add(patient);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading patients file: " + e.getMessage());
        }
        return patients;
    }
    public Patient getPatientByNationalID(String nationalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATIENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1);
                if (parts.length > 1 && parts[1].trim().equals(nationalID)) {
                    return parsePatient(parts);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading patients file: " + e.getMessage());
        }
        return null; // Patient not found
    }
    /**
     * Retrieves all appointments from the database, including patient name.
     * @return A list of Appointment objects.
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                // Ensure enough parts for a complete appointment record (8 fields expected based on `addAppointment`)
                if (parts.length >= 8) {
                    try {
                        appointments.add(new Appointment(
                            Integer.parseInt(parts[0].trim()),                            // id
                            parts[1].trim(),                                              // doctor_id
                            parts[2].trim(),                                              // patient_id
                            parts[3].trim(),                                             // field
                            LocalDateTime.parse(parts[6].trim(), DATE_TIME_FORMATTER),  // appointment_time
                            parts[4].trim(),                                             // doctor_name
                            parts[5].trim(),                                             // patient_name
                            parts[7].trim()                                              // room
                        ));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing appointment ID or related data: " + line + " - " + e.getMessage());
                    } catch (Exception e) { // Catch other parsing errors like DateTimeParseException
                        System.err.println("Error parsing appointment data (date/time/other): " + line + " - " + e.getMessage());
                    }
                } else {
                     System.err.println("Skipping malformed appointment line (not enough parts): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments file: " + e.getMessage());
        }
        return appointments;
    }
    /**
     * Retrieves appointments for a specific patient ID.
     * @param patientId The ID of the patient.
     * @return A list of Appointment objects for the given patient.
     */
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                // Ensure enough parts for a complete appointment record (8 fields expected based on `addAppointment`)
                if (parts.length >= 7) {
                    try {
                        String currentAppointmentPatientId = parts[2].trim();
                        if(currentAppointmentPatientId.equals(patientId)) {
                            //1|079111111111|079123123123|General Practice|Dr. John Smith|John Doe|2025-08-25 15:00:00|Room 1
                            appointments.add(new Appointment(
                            Integer.parseInt(parts[0].trim()),                            // id
                            parts[1].trim(),                             // doctor_id
                            parts[2].trim(),                             // patient_id
                            parts[3].trim(),                                             // field
                            LocalDateTime.parse(parts[6].trim(), DATE_TIME_FORMATTER),  // appointment_time
                            parts[4].trim(),                                             // doctor_name
                            parts[5].trim(),                                             // patient_name
                            parts[7].trim()                                              // room
                        ));
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing appointment ID or related data: " + line + " - " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Error parsing appointment data (date/time/other): " + line + " - " + e.getMessage());
                    }
                } else {
                     System.err.println("Skipping malformed appointment line (not enough parts): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments file: " + e.getMessage());
        }
        return appointments;
    }

    /**
     * Retrieves appointments for a specific doctor ID.
     * @param doctorId The ID of the doctor.
     * @return A list of Appointment objects for the given doctor.
     */
    public List<Appointment> getAppointmentsByDoctor(String doctorId) { // NEW METHOD
       List<Appointment> appointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                // Ensure enough parts for a complete appointment record (8 fields expected based on `addAppointment`)
                if (parts.length >= 8) {
                    try {
                        String currentAppointmentDoctorId = parts[1].trim();
                        if(currentAppointmentDoctorId == doctorId) {
                            appointments.add(new Appointment(
                            Integer.parseInt(parts[0].trim()),                            // id
                            parts[1].trim(),                             // doctorId
                           parts[2].trim(),                             // patientId
                            parts[3].trim(),                                             // field
                            LocalDateTime.parse(parts[6].trim(), DATE_TIME_FORMATTER),  // appointmentTime
                            parts[4].trim(),                                             // doctorName (stored directly)
                            parts[5].trim(),                                             // patientName (stored directly)
                            parts[7].trim()                                              // room
                        ));
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing appointment ID or related data: " + line + " - " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Error parsing appointment data (date/time/other): " + line + " - " + e.getMessage());
                    }
                } else {
                     System.err.println("Skipping malformed appointment line (not enough parts): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments file: " + e.getMessage());
        }
        return appointments;
    }
    /**
     * Updates an existing patient's information using file-based I/O.
     * @param patient The Patient object with updated information (patient_id must match an existing patient).
     * @return True if update was successful, false otherwise.
     */
    public boolean updatePatient(Patient patient) {
        List<String> lines = new ArrayList<>();
        boolean patientFound = false;
    
        // Read all lines and update the matching patient
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATIENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER, -1);
                // So sánh dựa trên dbId (id tự tăng trong file, là duy nhất)
                // parts[0] là dbId
                // if (parts.length > 0 && parts[0].trim().equals(String.valueOf(patient.getDbId()))) {
                // Nó sẽ xóa mọi ký tự không phải số trước khi so sánh
                System.out.println("Comparing file ID: '" + parts[0].replaceAll("[^\\d]", "") + "' with patient DB ID: '" + patient.getDbId() + "'");
                if (parts.length > 0 && parts[0].replaceAll("[^\\d]", "").equals(String.valueOf(patient.getDbId()))) {
                    // Tạo dòng đã được cập nhật
                    String updatedLine = String.join(DELIMITER,
                        parts[0].trim(), patient.getId(), patient.getPassword(),
                        patient.getName(), patient.getPhone(), patient.getDOB(),
                        patient.getGender(), patient.getGuardianRela(),
                        patient.getGuardianName(), patient.getGuardPhone()
                    );
                    lines.add(updatedLine);
                    patientFound = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading patients file for update: " + e.getMessage());
            return false;
        }
        
        if (!patientFound) {
            System.out.println("No patient found with ID: " + patient.getId() + " to update.");
            return false;
        }
        
        // Write all lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_PATIENTS))) {
            // for (int i = 0; i < lines.size(); i++) {
            //     writer.write(lines.get(i));
            //     if (i < lines.size() - 1) {
            //         writer.newLine();
            //     }
            // }
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // <-- Luôn thêm ký tự xuống dòng sau mỗi dòng
            }
            System.out.println("Patient ID " + patient.getId() + " updated successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing updated patients file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a patient and all their associated appointments using file-based I/O.
     * @param patientId The ID of the patient to delete.
     * @return True if deletion was successful, false otherwise.
     */
    public boolean deletePatient(String patientId) {
        boolean patientFound = false;
        
        // First, delete the patient from the patients file
        List<String> patientLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATIENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 6) {
                    String currentPatientId = parts[3].trim();
                    if (currentPatientId == patientId) {
                        patientFound = true;
                        // Skip this line (delete the patient)
                        continue;
                    }
                }
                patientLines.add(line); // Keep all other lines
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading patients file for deletion: " + e.getMessage());
            return false;
        }
        
        if (!patientFound) {
            System.out.println("No patient found with ID: " + patientId + " to delete.");
            return false;
        }
        
        // Write updated patients file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_PATIENTS))) {
            for (String line : patientLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing updated patients file: " + e.getMessage());
            return false;
        }
        
        // Now delete all associated appointments (cascade delete)
        List<String> appointmentLines = new ArrayList<>();
        int deletedAppointments = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_APPOINTMENTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + DELIMITER);
                if (parts.length >= 8) {
                    String currentPatientId = parts[2].trim(); // patient_id is at index 2
                    if (currentPatientId == patientId) {
                        deletedAppointments++;
                        // Skip this line (delete the appointment)
                        continue;
                    }
                }
                appointmentLines.add(line); // Keep all other lines
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading appointments file for cascade deletion: " + e.getMessage());
            // Patient was already deleted, so we continue but log the error
        }
        
        // Write updated appointments file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_APPOINTMENTS))) {
            for (String line : appointmentLines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing updated appointments file: " + e.getMessage());
            // Patient was already deleted, so we continue but log the error
        }
        
        System.out.println("Patient ID " + patientId + " and " + deletedAppointments + " associated appointments deleted successfully.");
        return true;
    }
}