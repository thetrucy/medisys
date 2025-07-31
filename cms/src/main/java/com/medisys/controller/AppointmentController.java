package com.medisys.controller;

import com.medisys.model.ScheduleMaker;
import com.medisys.Main;
import com.medisys.model.Appointment;
import com.medisys.model.Patient;
import com.medisys.model.Doctor;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
//import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.List;
import com.medisys.util.DatabaseManager;

public class AppointmentController implements Initializable {
    ScheduleMaker Appointments;
    private Doctor selectedDoctor;
    private DatabaseManager dbManager;
    private Stage stage;
	private Scene scene;
	private Parent root;
   // DatabaseManager appointedDoctors;
    static Integer appointmentID = 0;

    //radio buttons and toggle group
    @FXML private RadioButton radioSelf;
    @FXML private RadioButton radioOther;
    @FXML private ToggleGroup appointmentGroup;

    //form containers
    @FXML private VBox selfBookingForm;
    @FXML private VBox otherBookingForm;

    //self booking form fields
    @FXML private TextField nameFieldSelf;
    @FXML private TextField dobFieldSelf;
    @FXML private TextField phoneFieldSelf;
    @FXML private ComboBox<String> genderBoxSelf;
    @FXML private DatePicker appointmentDateSelf;
    @FXML private ComboBox<String> appointmentTimeSelf;
    @FXML private Button submitButtonSelf;

    //other booking form fields
    @FXML private TextField patientIdField; //----new
    @FXML private TextField patientNameField;
    @FXML private TextField patientDobField;
    @FXML private TextField patientPhoneField;
    @FXML private ComboBox<String> patientGenderBox;
    @FXML private ComboBox<String> relationshipBox;
    @FXML private TextField guardPhoneField;
    @FXML private TextField guardNameField;
    @FXML private DatePicker appointmentDateOther;
    @FXML private ComboBox<String> appointmentTimeOther;
    @FXML private Button submitButtonOther;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.dbManager = DatabaseManager.getInstance();
        //Reset time in combo box
        initializeTimeSlots();
        genderBoxSelf.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        patientGenderBox.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        relationshipBox.setItems(FXCollections.observableArrayList(
            "Cha/Mẹ", "Vợ/Chồng", "Con", "Anh/Chị/Em", "Khác"
        ));
        
        //default form
        showSelfBookingForm();
        Appointments = new ScheduleMaker();
       // appointedDoctors = new DatabaseManager();
        appointmentID = getNextAvailableAppointmentId();
        loadExistingAppointments();

        appointmentDateSelf.valueProperty().addListener((obs, oldDate, newDate) -> {
        updateTimeSlots(appointmentTimeSelf, appointmentDateSelf);
        });
    
        appointmentDateOther.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateTimeSlots(appointmentTimeOther, appointmentDateOther);
        });

        setupTimeSlotSelectionHandlers();
    }

    // Add the enum for blur types
    private enum BlurType {
        GAUSSIAN, BOX, SHADOW, COMBINED, NONE
    }

    private void initializeTimeSlots() {
        //time slots for appointments
        String[] timeSlots = {
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
            "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00"
        };
        
        
        appointmentTimeSelf.setItems(FXCollections.observableArrayList(timeSlots));
        appointmentTimeOther.setItems(FXCollections.observableArrayList(timeSlots));
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com/medisys/view/AppointmentOne_1.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
    }

    private Integer getNextAvailableAppointmentId() {
        if (Appointments.getAppointments().isEmpty()) {
            return 1;
        }
        return Appointments.getAppointments().stream()
            .mapToInt(Appointment::getId)
            .max()
            .orElse(0) + 1;
    }

    private boolean saveAppointmentToDatabase(Appointment appointment) {
        try {
            System.out.println("Attempting to save appointment: " + appointment);
            int appointmentId = dbManager.addAppointment(appointment);          
            if (appointmentId > 0) {
                appointment.setID(appointmentId);
                Appointments.addAppointment(appointment);
                return true;
            } else {
                System.err.println("Failed to save appointment (returned false)");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Database error saving appointment:");
            e.printStackTrace();
            return false;
        }
    }

    private void loadExistingAppointments() {
        try{
            List<Appointment> dbAppointments = dbManager.getAllAppointments();
            for (Appointment tempApp : dbAppointments) {
                Appointments.addAppointment(tempApp);
            }
        } catch (Exception e) {
            System.err.println("Error loading existing appointments: " + e.getMessage());
        }
    }
    
    public void setSelectedDoctor(Doctor doctor) {
        this.selectedDoctor = doctor;
    }

    private void setupTimeSlotSelectionHandlers() {
        appointmentTimeSelf.setOnAction(event -> {
            String selectedTime = appointmentTimeSelf.getValue();
            if (selectedTime != null && selectedTime.contains("(Đã đặt)")) {
                appointmentTimeSelf.setValue(null);
            }
        });
        
        appointmentTimeOther.setOnAction(event -> {
            String selectedTime = appointmentTimeOther.getValue();
            if (selectedTime != null && selectedTime.contains("(Đã đặt)")) {
                appointmentTimeOther.setValue(null);
            }
        });
    }

    private Set<String> getOccupiedTimeSlots(LocalDate date, Doctor doctor) {
        Set<String> occupiedSlots = new HashSet<>();
        
        if (Appointments != null && date != null && doctor != null) {
            for (Appointment appointment : Appointments.getAppointments()) {
                if (appointment.getDoctorId() == doctor.getId() && 
                    appointment.getAppointmentTime() != null && // Add null check
                    appointment.getAppointmentTime().toLocalDate().equals(date)) {
                    
                    String timeSlot = appointment.getAppointmentTime().toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
                    occupiedSlots.add(timeSlot);
                }
            }
        }
        
        return occupiedSlots;
    }

    private Callback<ListView<String>, ListCell<String>> createTimeSlotCellFactory(
    DatePicker datePicker, Doctor doctor) {
        return new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty || item == null) {
                            setText(null);
                            setDisable(false);
                            setStyle("");
                            setEffect(null);
                        } else {
                            setText(item);
                            
                            // Check if this time slot is occupied
                            LocalDate selectedDate = datePicker.getValue();
                            if (selectedDate != null && doctor != null) {
                                Set<String> occupiedSlots = getOccupiedTimeSlots(selectedDate, doctor);
                                
                                if (occupiedSlots.contains(item)) {
                                    setDisable(true);
                                    
                                    // Apply blur effect
                                    applyBlurEffect(this, BlurType.COMBINED);
                                    
                                    // Apply styling
                                    setStyle("-fx-opacity: 0.5; -fx-background-color: #f0f0f0; " +
                                            "-fx-text-fill: #999999; -fx-background-radius: 3; " +
                                            "-fx-border-color: #cccccc; -fx-border-radius: 3; " +
                                            "-fx-border-width: 1;");
                                            
                                    setText(item + " (Đã đặt)");
                                } else {
                                    setDisable(false);
                                    setStyle("");
                                    setEffect(null);
                                    setText(item);
                                }
                            }
                        }
                    }
                };
            }
        };
    }

    // Method to update time slots when date changes
    private void updateTimeSlots(ComboBox<String> timeComboBox, DatePicker datePicker) {
        if (selectedDoctor != null && datePicker.getValue() != null) {
            // Clear current selection
            timeComboBox.setValue(null);
            
            // Set the cell factory to handle disabled slots
            timeComboBox.setCellFactory(createTimeSlotCellFactory(datePicker, selectedDoctor));
            
            // Also set button cell to show disabled state in the button
            timeComboBox.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        LocalDate selectedDate = datePicker.getValue();
                        if (selectedDate != null && selectedDoctor != null) {
                            Set<String> occupiedSlots = getOccupiedTimeSlots(selectedDate, selectedDoctor);
                            
                            if (occupiedSlots.contains(item)) {
                                setText(item + " (Đã đặt)");
                                setStyle("-fx-text-fill: #666666;");
                            } else {
                                setText(item);
                                setStyle("");
                            }
                        }
                    }
                }
            });
        }
    }

    public void refreshTimeSlots() {
        if (appointmentDateSelf.getValue() != null) {
            updateTimeSlots(appointmentTimeSelf, appointmentDateSelf);
        }
        if (appointmentDateOther.getValue() != null) {
            updateTimeSlots(appointmentTimeOther, appointmentDateOther);
        }
    }

    private void applyBlurEffect(ListCell<String> cell, BlurType blurType) {
        switch (blurType) {
            case GAUSSIAN:
                GaussianBlur gaussianBlur = new GaussianBlur(3.0);
                cell.setEffect(gaussianBlur);
                break;
                
            case BOX:
                BoxBlur boxBlur = new BoxBlur(3.0, 3.0, 2);
                cell.setEffect(boxBlur);
                break;
                
            case SHADOW:
                DropShadow dropShadow = new DropShadow();
                dropShadow.setRadius(5.0);
                dropShadow.setOffsetX(2.0);
                dropShadow.setOffsetY(2.0);
                dropShadow.setColor(Color.color(0.4, 0.4, 0.4, 0.6));
                cell.setEffect(dropShadow);
                break;
                
            case COMBINED:
                // Combine blur and shadow
                GaussianBlur combinedBlur = new GaussianBlur(2.0);
                DropShadow combinedShadow = new DropShadow();
                combinedShadow.setRadius(3.0);
                combinedShadow.setOffsetX(1.0);
                combinedShadow.setOffsetY(1.0);
                combinedShadow.setColor(Color.color(0.3, 0.3, 0.3, 0.5));
                combinedShadow.setInput(combinedBlur);
                cell.setEffect(combinedShadow);
                break;
                
            default:
                cell.setEffect(null);
        }
    }

    

    @FXML
    private void onRadioChange(ActionEvent event) {
        if (radioSelf.isSelected()) {
            showSelfBookingForm();
        } else if (radioOther.isSelected()) {
            showOtherBookingForm();
        }
    }

    private void showSelfBookingForm() {
        selfBookingForm.setVisible(true);
        selfBookingForm.setManaged(true);
        otherBookingForm.setVisible(false);
        otherBookingForm.setManaged(false);
    }

    private void showOtherBookingForm() {
        selfBookingForm.setVisible(false);
        selfBookingForm.setManaged(false);
        otherBookingForm.setVisible(true);
        otherBookingForm.setManaged(true);
    }

    @SuppressWarnings("unused")
	@FXML
    private void handleSubmitSelf(ActionEvent event) {
        if (validateSelfBookingForm()) {
            try {
                String name = nameFieldSelf.getText().trim();
                String dob = dobFieldSelf.getText().trim();
                String phone = phoneFieldSelf.getText().trim();
                LocalDate appointmentDate = appointmentDateSelf.getValue();
                String appointmentTime = appointmentTimeSelf.getValue();

                if (name != null && !name.isEmpty() && 
                    phone != null && !phone.isEmpty()) {

                    Patient patient = new Patient(name, phone, dob);

                    // Debug: Print patient info
                    System.out.println("Created patient: " + patient);

                    // Add patient to database
                    Integer id = Appointments.getData().addPatient(patient);
                    
                    if (patient == null) {
                        showErrorAlert("Lỗi", "Không thể tạo thông tin bệnh nhân.");
                        return;
                    }

                    if (id == null || id <= 0) {
                        showErrorAlert("Lỗi", "Không thể thêm bệnh nhân vào cơ sở dữ liệu.");
                        return;
                    }

                    // Set patient ID
                    patient.setID(id);

                    // Check if doctor is selected
                    if (selectedDoctor == null || appointmentDate == null || appointmentTime == null) {
                        showErrorAlert("Thiếu thông tin", "Vui lòng kiểm tra lại thông tin bác sĩ và ngày giờ hẹn.");
                        return;
                    }

                    // Combine date and time
                    LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));

                    // Create appointment
                    Appointment appointment = new Appointment(
                        appointmentID++,
                        selectedDoctor.getId(),
                        id,
                        selectedDoctor.getFaculty(),
                        appointmentDateTime,
                        selectedDoctor.getName(),
                        name,
                        selectedDoctor.getRoom()
                    );

                    // database funny funny
                    boolean addedSuccessfully = saveAppointmentToDatabase(appointment);

                    if (addedSuccessfully) {
                        showSuccessAlert("Đặt lịch thành công!",
                            "Lịch hẹn của bạn đã được đặt thành công.\n" +
                            "Ngày: " + appointmentDate + "\n" +
                            "Giờ: " + appointmentTime);

                        refreshTimeSlots();
                        clearSelfForm();
                    } else {
                        showErrorAlert("Đặt lịch thất bại", "Buổi hẹn này đã được đặt trước hoặc có lỗi xảy ra.");
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                showErrorAlert("Lỗi hệ thống", "Thông tin bệnh nhân không hợp lệ. Vui lòng thử lại.");
                return;
            }
        }
    }

    @FXML
    private void handleSubmitOther(ActionEvent event) {
        if (validateOtherBookingForm()) {
            try {
                String idText = patientIdField.getText().trim();
                String patientName = patientNameField.getText().trim();
                String patientDob = patientDobField.getText().trim();
                String patientPhone = patientPhoneField.getText().trim();
                String relationship = relationshipBox.getValue();
                String guardName = guardNameField.getText().trim();
                String guardPhone = guardPhoneField.getText().trim();
                LocalDate appointmentDate = appointmentDateOther.getValue();
                String appointmentTime = appointmentTimeOther.getValue();

                // Validate required fields
                if (idText.isEmpty() || patientName.isEmpty() || patientPhone.isEmpty() || 
                    relationship == null || guardName.isEmpty() || guardPhone.isEmpty() ||
                    appointmentDate == null || appointmentTime == null) {
                    showErrorAlert("Thiếu thông tin", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                // Create and save patient
                Long patientId = Long.parseLong(idText);
                Patient patient = new Patient(patientId, patientName, patientPhone, patientDob);
                patient.setGuard(relationship, guardName, guardPhone);
                
                Integer id = Appointments.getData().addPatient(patient);
                if (id == null || id <= 0) {
                    showErrorAlert("Lỗi", "Không thể thêm bệnh nhân vào cơ sở dữ liệu.");
                    return;
                }
                patient.setID(id); // This was missing!

                // Verify doctor selected
                if (selectedDoctor == null) {
                    showErrorAlert("Thiếu thông tin", "Vui lòng chọn bác sĩ trước khi đặt lịch.");
                    return;
                }

                // Create appointment
                LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));
                Appointment appointment = new Appointment(
                    appointmentID++, 
                    selectedDoctor.getId(), 
                    patientId, 
                    selectedDoctor.getFaculty(), 
                    appointmentDateTime, 
                    selectedDoctor.getName(), 
                    patientName, 
                    selectedDoctor.getRoom()
                );

                // Single save attempt (remove the redundant saveToDatabase call)
                boolean addedSuccessfully = saveAppointmentToDatabase(appointment);
                
                if (addedSuccessfully) {
                    showSuccessAlert("Đặt lịch thành công!",
                        "Lịch hẹn của bạn đã được đặt thành công.\n" +
                        "Ngày: " + appointmentDate + "\n" +
                        "Giờ: " + appointmentTime);
                    
                    refreshTimeSlots();
                    clearOtherForm();
                } else {
                    showErrorAlert("Đặt lịch thất bại", "Buổi hẹn này đã được đặt trước hoặc có lỗi xảy ra.");
                }

            } catch (Exception e) {
                showErrorAlert("Lỗi không mong muốn", "Đã xảy ra lỗi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private boolean validateSelfBookingForm() {
        StringBuilder errors = new StringBuilder();

        if (nameFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập họ và tên\n");
        }
        if (dobFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập ngày sinh\n");
        }
        if (phoneFieldSelf.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại\n");
        }
        if (genderBoxSelf.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }
        if (appointmentDateSelf.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        }
        if (appointmentTimeSelf.getValue() == null) {
            errors.append("- Vui lòng chọn giờ khám\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Thông tin chưa đầy đủ", errors.toString());
            return false;
        }
        return true;
    }

    private boolean validateOtherBookingForm() {
        StringBuilder errors = new StringBuilder();

        if (patientIdField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập CMND/CCCD bệnh nhân\n");
        }
        if (patientNameField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập họ và tên bệnh nhân\n");
        }
        if (patientDobField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập ngày sinh bệnh nhân\n");
        }
        if (patientPhoneField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại bệnh nhân\n");
        } 
            
        if (patientGenderBox.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }
        if (relationshipBox.getValue() == null) {
            errors.append("- Vui lòng chọn mối quan hệ với bệnh nhân\n");
        }
        if (guardPhoneField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại người giám hộ\n");
        }
        if (guardNameField.getText().trim().isEmpty()) {
            errors.append("- Vui lòng nhập tên người giám hộ\n");
        }
        if (appointmentDateOther.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        }
        if (appointmentTimeOther.getValue() == null) {
            errors.append("- Vui lòng chọn giờ khám\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Thông tin chưa đầy đủ", errors.toString());
            return false;
        }
        return true;
    }

    private void clearSelfForm() {
        nameFieldSelf.clear();
        dobFieldSelf.clear();
        phoneFieldSelf.clear();
        genderBoxSelf.setValue(null);
        appointmentDateSelf.setValue(LocalDate.now());
        appointmentTimeSelf.setValue(null);
    }

    private void clearOtherForm() {
        patientIdField.clear();
        patientNameField.clear();
        patientDobField.clear();
        patientPhoneField.clear();
        patientGenderBox.setValue(null);
        relationshipBox.setValue(null);
        guardPhoneField.clear();
        guardNameField.clear();
        appointmentDateOther.setValue(LocalDate.now());
        appointmentTimeOther.setValue(null);
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void goBackToAppointmentOne(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/AppointmentOne_1.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onAppointmentsButtonClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/UpcomingApm.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/medisys/view/PatientProfile.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}