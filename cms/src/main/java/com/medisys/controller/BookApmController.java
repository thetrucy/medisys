package com.medisys.controller;

import com.medisys.model.ScheduleMaker;

import com.medisys.model.Appointment;
import com.medisys.model.Patient;
import com.medisys.model.Doctor;
import com.medisys.model.User;
import com.medisys.util.CurrentUser;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.control.*;
//import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
// import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.List;
import com.medisys.util.DatabaseManager;

public class BookApmController implements Initializable {
    // ====== ADDED ======
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    // ===================
    ScheduleMaker Appointments;
    private Doctor selectedDoctor;
    private Patient curPatient;
    private DatabaseManager dbManager;

    // private Stage stage;
	// private Scene scene;
	// private Parent root;
    DatabaseManager appointedDoctors;
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
  //  @FXML private TextField patientPhoneField;
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
        User CurUser = CurrentUser.getInstance().getCurrentUser();
        curPatient = (Patient) CurUser;     

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
        System.out.println("[DEBUG] Initializing time slots: " + String.join(", ", timeSlots));
        appointmentTimeSelf.setItems(FXCollections.observableArrayList(timeSlots));
        appointmentTimeOther.setItems(FXCollections.observableArrayList(timeSlots));
    }

    @FXML
    private void handleBackAction(ActionEvent event) throws IOException {
        try {
            mainController.loadAppointmentFirstView();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // Check for double-booking
            for (Appointment existing : Appointments.getAppointments()) {
                if (existing.getDoctorId().equals(appointment.getDoctorId()) &&
                    existing.getAppointmentTime() != null &&
                    appointment.getAppointmentTime() != null &&
                    existing.getAppointmentTime().equals(appointment.getAppointmentTime())) {
                    System.err.println("[ERROR] Double-booking detected for doctor " + appointment.getDoctorId() + " at " + appointment.getAppointmentTime());
                    return false;
                }
            }

            boolean successful = Appointments.addAppointment(appointment);
            if (successful) {
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

    private void autoFillSelfBookingForm() {
        if (curPatient != null) {
            String name = curPatient.getName();
            if (name != null && !name.isEmpty()) {
                nameFieldSelf.setText(name);
                nameFieldSelf.setDisable(true);
            }
            
            String dob = curPatient.getDOB();
            if (dob != null && !dob.isEmpty()) {
                dobFieldSelf.setText(dob);
                dobFieldSelf.setDisable(true);
            }
            
            String phone = curPatient.getPhone();
            if (phone != null && !phone.isEmpty()) {
                phoneFieldSelf.setText(phone);
                phoneFieldSelf.setDisable(true);
            }

            String gender = curPatient.getGender();
            if (gender != null && !gender.isEmpty()) {
                genderBoxSelf.setValue(gender);
                genderBoxSelf.setDisable(true);
            }
        }
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
                if (appointment.getDoctorId().equals(doctor.getId()) && 
                    appointment.getAppointmentTime() != null &&
                    appointment.getAppointmentTime().toLocalDate().equals(date)) {

                    String timeSlot = appointment.getAppointmentTime().toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
                    occupiedSlots.add(timeSlot);
                    System.out.println("[DEBUG] Occupied slot detected: " + timeSlot);
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
            System.out.println("[DEBUG] updateTimeSlots called for doctor " + selectedDoctor.getId() + " on date " + datePicker.getValue());
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
                            System.out.println("[DEBUG] updateTimeSlots: Checking slot " + item + " against occupied " + occupiedSlots);
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
        autoFillSelfBookingForm();
    }

    private void showOtherBookingForm() {
        selfBookingForm.setVisible(false);
        selfBookingForm.setManaged(false);
        otherBookingForm.setVisible(true);
        otherBookingForm.setManaged(true);

        // ADDED: Auto-fill guardian info from the current logged-in patient
        if (curPatient != null) {
            guardNameField.setText(curPatient.getName());
            guardPhoneField.setText(curPatient.getPhone());
            // You might want to disable these fields so the user can't change them
            guardNameField.setDisable(true);
            guardPhoneField.setDisable(true);
        }
    }

    @SuppressWarnings("unused")
	@FXML
    private void handleSubmitSelf(ActionEvent event) {
        if (validateSelfBookingForm()) {
            try {
                LocalDate appointmentDate = appointmentDateSelf.getValue();
                String appointmentTime = appointmentTimeSelf.getValue();

                if (selectedDoctor == null) {
                    showErrorAlert("Thiếu thông tin", "Vui lòng chọn bác sĩ.");
                    return;
                }

                LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));
                
                // Sử dụng trực tiếp thông tin từ curPatient
                Appointment appointment = new Appointment(
                    0, 
                    selectedDoctor.getId(), 
                    curPatient.getId(), 
                    selectedDoctor.getFaculty(), 
                    appointmentDateTime, 
                    selectedDoctor.getName(), 
                    curPatient.getName(), 
                    selectedDoctor.getRoom()
                );

                boolean addedSuccessfully = saveAppointmentToDatabase(appointment);
                if (addedSuccessfully) {
                    showSuccessAlert("Đặt lịch thành công!", "Lịch hẹn của bạn đã được đặt thành công.");
                    refreshTimeSlots();
                } else {
                    showErrorAlert("Đặt lịch thất bại", "Buổi hẹn này đã được đặt trước hoặc có lỗi xảy ra.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Lỗi hệ thống", "Đã có lỗi xảy ra. Vui lòng thử lại.");
            }
        }
    }

    @FXML
    private void handleSubmitOther(ActionEvent event) {
        if (validateOtherBookingForm()) {
            try {
                String patientId = patientIdField.getText().trim();
                String patientName = patientNameField.getText().trim();
                String patientDob = patientDobField.getText().trim();
                String patientGender = patientGenderBox.getValue();
                String relationship = relationshipBox.getValue();
                String guardName = guardNameField.getText().trim();
                String guardPhone = guardPhoneField.getText().trim();
                LocalDate appointmentDate = appointmentDateOther.getValue();
                String appointmentTime = appointmentTimeOther.getValue();

                // Validate required fields
                if (patientId.isEmpty() || patientName.isEmpty() ||
                    relationship == null || guardName.isEmpty() || guardPhone.isEmpty() ||
                    appointmentDate == null || appointmentTime == null) {
                    showErrorAlert("Thiếu thông tin", "Vui lòng điền đầy đủ thông tin");
                    return;
                }

                // Create and save patient
                Patient patient = Patient.createForOtherBooking(patientId, patientName, guardPhone, patientDob, patientGender);
                patient.setGuard(relationship, guardName, guardPhone);
                
                if (dbManager.getPatientByNationalID(patientId) == null) {
                    dbManager.addPatient(patient);
                }

                // Verify doctor selected
                if (selectedDoctor == null) {
                    showErrorAlert("Thiếu thông tin", "Vui lòng chọn bác sĩ trước khi đặt lịch.");
                    return;
                }

                // Create appointment
                LocalDateTime appointmentDateTime = appointmentDate.atTime(LocalTime.parse(appointmentTime));
                Appointment appointment = new Appointment(
                    0, 
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

        String name = nameFieldSelf.getText().trim();
        if (name.isEmpty()) {
            errors.append("- Không tìm thấy họ tên trong CSDL\n");
        }

        String dob = dobFieldSelf.getText().trim();
        if (dob.isEmpty()) {
            errors.append("- Không tìm thấy ngày sinh trong CSDL\n");
        }

        String phone = phoneFieldSelf.getText().trim();
        if (phone.isEmpty()) {
            errors.append("- Không tìm thấy số điện thoại trong CSDL\n");
        } else if (!isValidPhoneNumber(phone)) {
            errors.append("- Số điện thoại trong CSDL không hợp lệ\n");
        }


        if (genderBoxSelf.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }

        if (appointmentDateSelf.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        } else if (!isValidAppointmentDate(appointmentDateSelf.getValue())) {
            errors.append("- Ngày khám phải từ hôm nay trở đi\n");
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

        String id = patientIdField.getText().trim();
        if (id.isEmpty()) {
            errors.append("- Vui lòng nhập CMND/CCCD bệnh nhân\n");
        } else if (!isValidPatientId(id)) {
            errors.append("- CMND/CCCD không hợp lệ (9 hoặc 12 chữ số)\n");
        }

        String name = patientNameField.getText().trim();
        if (name.isEmpty()) {
            errors.append("- Vui lòng nhập họ và tên bệnh nhân\n");
        } else if (name.length() < 2) {
            errors.append("- Họ và tên phải có ít nhất 2 ký tự\n");
        } else if (name.length() > 50) {
            errors.append("- Họ và tên không được vượt quá 50 ký tự\n");
        } else if (!name.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            errors.append("- Họ và tên chỉ được chứa chữ cái và khoảng trắng\n");
        }

        String dob = patientDobField.getText().trim();
        if (dob.isEmpty()) {
            errors.append("- Vui lòng nhập ngày sinh bệnh nhân\n");
        } else if (!isValidDateFormat(dob)) {
            errors.append("- Ngày sinh bệnh nhân không đúng định dạng (dd/MM/yyyy) hoặc chưa đến ngày này\n");
        }

        // String patientPhone = patientPhoneField.getText().trim();
        // if (patientPhone.isEmpty()) {
        //     errors.append("- Vui lòng nhập số điện thoại bệnh nhân\n");
        // } else if (!isValidPhoneNumber(patientPhone)) {
        //     errors.append("- Số điện thoại bệnh nhân không hợp lệ (10-11 chữ số, bắt đầu bằng 0)\n");
        // } 
            
        if (patientGenderBox.getValue() == null) {
            errors.append("- Vui lòng chọn giới tính\n");
        }

        if (relationshipBox.getValue() == null) {
            errors.append("- Vui lòng chọn mối quan hệ với bệnh nhân\n");
        }

        String phoneG = guardPhoneField.getText().trim();
        if (phoneG.isEmpty()) {
            errors.append("- Vui lòng nhập số điện thoại người giám hộ\n");
        } else if (!isValidPhoneNumber(phoneG)) {
            errors.append("- Số điện thoại người giám hộ không hợp lệ (10-11 chữ số, bắt đầu bằng 0)\n");
        }

        String nameG = guardNameField.getText().trim();
        if (nameG.isEmpty()) {
            errors.append("- Vui lòng nhập tên người giám hộ\n");
        } else if (nameG.length() < 2) {
            errors.append("- Tên người giám hộ phải có ít nhất 2 ký tự\n");
        } else if (nameG.length() > 50) {
            errors.append("- Tên người giám hộ không được vượt quá 50 ký tự\n");
        } else if (!nameG.matches("^[a-zA-ZÀ-ỹ\\s]+$")) {
            errors.append("- Tên người giám hộ chỉ được chứa chữ cái và khoảng trắng\n");
        }

        if (appointmentDateOther.getValue() == null) {
            errors.append("- Vui lòng chọn ngày khám\n");
        } else if (!isValidAppointmentDate(appointmentDateOther.getValue())) {
            errors.append("- Ngày khám phải từ hôm nay trở đi\n");
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

    //validations
    private boolean isValidPatientId(String id) {
        return id.matches("^\\d{9}$") || id.matches("^\\d{12}$");
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^0\\d{9,10}$");
    }

    private boolean isValidDateFormat(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.SMART);
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // private boolean isValidAge(String dateStr) {
    //     try {
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.SMART);
    //         LocalDate birthDate = LocalDate.parse(dateStr, formatter);
    //         LocalDate currentDate = LocalDate.now();
            
    //         int age = Period.between(birthDate, currentDate).getYears();
            
    //         return age >= 1 && age <= 120 && !birthDate.isAfter(currentDate);
    //     } catch (DateTimeParseException e) {
    //         return false;
    //     }
    // }

    private boolean isValidAppointmentDate(Object dateValue) {
        if (dateValue instanceof LocalDate) {
            LocalDate appointmentDate = (LocalDate) dateValue;
            return !appointmentDate.isBefore(LocalDate.now());
        }
        return false;
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
     //   patientPhoneField.clear();
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
}