package com.medisys.controller;

import com.medisys.util.DatabaseManager;
import com.medisys.model.Doctor;
import java.util.List;


import java.io.IOException;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.collections.FXCollections; 
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import com.medisys.Main;

public class AppointmentOne_1_Ctrler {
	private Stage stage;
	private Scene scene;
	private Parent root;
	private FilteredList<Doctor> filteredData;
	private SortedList<Doctor> sortedData;

    @FXML
    private TableView<Doctor> doctorTable;
    
    @FXML
    private Button buttonA;

    @FXML
    private Button buttonB;

    @FXML
    private TableColumn<Doctor, Integer> Id;
    @FXML
    private TableColumn<Doctor, String> Name;
    //@FXML
    //private TableColumn<Doctor, String> Phone;
    @FXML
    private TableColumn<Doctor, String> Gmail;

    @FXML
    private TableColumn<Doctor, String> Faculty;
    
    @FXML
    private TableColumn<Doctor, String> Room;
    
    @FXML
    private TextField FilterField;

    private ObservableList<Doctor> masterData = FXCollections.observableArrayList();
    private DatabaseManager dbManager;

    @FXML private Button homeButton;
    @FXML private Button appointmentsButton;
    @FXML private Button profileButton;

    public AppointmentOne_1_Ctrler() {
        // Constructor, leave empty for FXML injection.
        this.dbManager = DatabaseManager.getInstance();
    }
    @FXML
    public void initialize() {

        try {
            List<Doctor> doctorsFromDb = dbManager.getAllDoctors();
            masterData.addAll(doctorsFromDb);
            System.out.println("Loaded " + doctorsFromDb.size() + " doctors from the database.");
        } catch (Exception e) {
            System.err.println("Error initializing doctor data from database: " + e.getMessage());
            e.printStackTrace();
        }

        // Setup columns
        Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        //Phone.setCellFactory(new PropertyValueFactory<>("phone"));
        Gmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        Faculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        Room.setCellValueFactory(new PropertyValueFactory<>("room"));

        // Wrap in filtered list
        filteredData = new FilteredList<>(masterData, p -> true);
        FilterField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String lower = newVal.toLowerCase();
            filteredData.setPredicate(doctor ->
                doctor.getName().toLowerCase().contains(lower) ||
                doctor.getFaculty().toLowerCase().contains(lower)
            );
        });

        // Sorting
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(doctorTable.comparatorProperty());
        doctorTable.setItems(sortedData);

        setupRowClickHandler();
    }
    
    public void switchToScene1(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/view/AppointmentOne_1.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToScene2(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/view/AppointmentOne_2.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

    public void switchToAppointmentBooking(Doctor selectedDoctor, javafx.scene.input.MouseEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader (getClass().getResource("/com/medisys/view/Appointment.fxml"));
        root = loader.load();

        AppointmentController appointmentController = loader.getController();
        appointmentController.setSelectedDoctor(selectedDoctor);

		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
   
    @FXML
    private void setupRowClickHandler() {
    doctorTable.setRowFactory(tv -> {
        javafx.scene.control.TableRow<Doctor> row = new javafx.scene.control.TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                Doctor selectedDoctor = row.getItem();
                try {
                    switchToAppointmentBooking(selectedDoctor, event);
                } catch (IOException e) {
                    System.err.println("Error switching scene: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        return row;
    });
}

	@SuppressWarnings("unchecked")
	@FXML
	private void handleButtonClick(javafx.event.ActionEvent event) {
	    Button clicked = (Button) event.getSource();

	    if (clicked == buttonA) {
	        setActive(buttonA);
	        setInactive(buttonB);
	        animatePop(buttonA);

	        // Sort by Name
	        doctorTable.getSortOrder().setAll(Name); // <--
	        Name.setSortType(TableColumn.SortType.ASCENDING); // <--

	    } else if (clicked == buttonB) {
	        setActive(buttonB);
	        setInactive(buttonA);
	        animatePop(buttonB);

	        // Sort by Faculty
	        doctorTable.getSortOrder().setAll(Faculty); // <--
	        Faculty.setSortType(TableColumn.SortType.ASCENDING); // <--
	    }
	}

	// below here are just buttons animation

    private void setActive(Button btn) {
    	System.out.println("lmao on");
    	Timeline timeline = new Timeline(
    	        new KeyFrame(Duration.millis(100),
    	            e -> btn.setStyle("-fx-background-color: #3c83c6; -fx-text-fill: white; -fx-background-radius: 20px;"),
    	            new KeyValue(btn.opacityProperty(), 1.0)
    	        )
    	    );
    	    timeline.play();
    }

    private void setInactive(Button btn) {
    	System.out.println("lmao off");
    	Timeline timeline = new Timeline(
    	        new KeyFrame(Duration.millis(100),
    	            e -> btn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-background-radius: 20px;"),
    	            new KeyValue(btn.opacityProperty(), 1.0)
    	        )
    	    );
    	    timeline.play();
    }
    
    private void animatePop(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.09);
        st.setToY(1.09);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    @FXML
    private void onHomeButtonClick(ActionEvent event) {
        try {
            Main.setRoot("AppointmentOne_1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAppointmentsButtonClick(ActionEvent event) {
        try {
            Main.setRoot("UpcomingAppointments"); 
       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) {
        try {
            Main.setRoot("PatientProfile");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewProfileButtonClick(ActionEvent event) {
        try {
            switchToScene2(event);
        } catch (IOException e) {
            System.err.println("Error switching to profile scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}