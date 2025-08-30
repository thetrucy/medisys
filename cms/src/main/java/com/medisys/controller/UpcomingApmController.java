package com.medisys.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.medisys.util.DatabaseManager; // Import DatabaseManager for database operations
import com.medisys.util.CurrentUser;
import com.medisys.model.Appointment;
import com.medisys.model.Patient;
import com.medisys.model.UpcomingAppointment;
import com.medisys.model.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;


public class UpcomingApmController implements Initializable {
	@FXML
    private TextField FilterField;

    @FXML
    private TableView<UpcomingAppointment> appointmentTable;

    @FXML
    private TableColumn<UpcomingAppointment, String> dateColumn;

    @FXML private TableColumn<UpcomingAppointment, String> patientColumn;

    @FXML
    private TableColumn<UpcomingAppointment, String> roomColumn;
    @FXML
    private TableColumn<UpcomingAppointment, String> departmentColumn;
    @FXML
    private TableColumn<UpcomingAppointment, String> doctorColumn;
    @FXML
    private TableColumn<UpcomingAppointment, String> notesColumn;

    private ObservableList<UpcomingAppointment> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        
        User currentUser = CurrentUser.getInstance().getCurrentUser();
        if (currentUser == null) {
            appointmentTable.setItems(FXCollections.observableArrayList());
            return;
        }

        loadAppointmentData(currentUser);
        setupFilter();
        appointmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        notesColumn.setMaxWidth(Double.MAX_VALUE);
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    private void loadAppointmentData(User currentUser) {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        List<Appointment> allRelatedAppointments = findRelatedAppointments(currentUser, dbManager);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Appointment apm : allRelatedAppointments) {
            String note = "";
            // Nếu tên bệnh nhân trong lịch hẹn khác tên người đăng nhập, đó là lịch đặt hộ
            if (!apm.getPatientName().equals(currentUser.getName())) {
                note = "Đặt hộ";
            }
            
            masterData.add(new UpcomingAppointment(
                apm.getAppointmentTime() != null ? apm.getAppointmentTime().format(formatter) : "",
                apm.getPatientName() != null ? apm.getPatientName() : "",
                apm.getRoom() != null ? apm.getRoom() : "",
                apm.getField() != null ? apm.getField() : "",
                apm.getDoctorName() != null ? apm.getDoctorName() : "",
                note
            ));
        }
    }

    private void setupFilter() {
        FilteredList<UpcomingAppointment> filteredData = new FilteredList<>(masterData, p -> true);

        FilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(appointment -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (appointment.getDoctor().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (appointment.getPatientName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (appointment.getDepartment().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        appointmentTable.setItems(filteredData);
    }

    private List<Appointment> findRelatedAppointments(User currentUser, DatabaseManager dbManager) {
        List<Appointment> allAppointments = dbManager.getAllAppointments();
        List<Patient> allPatients = dbManager.getAllPatients();
        List<Appointment> relatedAppointments = new ArrayList<>();

        // Danh sách các ID bệnh nhân liên quan (bao gồm cả chính người dùng và người được họ giám hộ)
        List<String> patientIdsToFind = new ArrayList<>();
        patientIdsToFind.add(currentUser.getId()); // 1. Thêm ID của chính người dùng

        // 2. Tìm các bệnh nhân mà người dùng này đang là người giám hộ
        for (Patient p : allPatients) {
            // Dùng Objects.equals để so sánh an toàn, tránh lỗi NullPointerException
            // và đảm bảo tên + SĐT người giám hộ không rỗng VÀ trùng khớp
            boolean isGuardianNameMatch = p.getGuardianName() != null && !p.getGuardianName().isEmpty() && p.getGuardianName().equals(currentUser.getName());
            boolean isGuardianPhoneMatch = p.getGuardPhone() != null && !p.getGuardPhone().isEmpty() && p.getGuardPhone().equals(currentUser.getPhone());

            if (isGuardianNameMatch && isGuardianPhoneMatch) {
                patientIdsToFind.add(p.getId()); // Thêm ID của người được giám hộ vào danh sách
            }
        }

        // 3. Lọc ra các cuộc hẹn của những bệnh nhân có trong danh sách
        for (Appointment apm : allAppointments) {
            if (patientIdsToFind.contains(apm.getPatientId())) {
                relatedAppointments.add(apm);
            }
        }
        
        // Sắp xếp lại theo thời gian
        relatedAppointments.sort((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()));
        return relatedAppointments;
    }
}