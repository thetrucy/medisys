package com.medisys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

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
    private TableColumn<Doctor, String> Name;

    @FXML
    private TableColumn<Doctor, String> Gmail;

    @FXML
    private TableColumn<Doctor, String> Faculty;

    @FXML
    private TableColumn<Doctor, String> Room;

    @FXML
    private TextField FilterField;

    private ObservableList<Doctor> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	try {
    		// imagine the C++ way to check the file
            InputStream input = getClass().getResourceAsStream("/com/medisys/doctors.txt");
            if (input == null) {
                System.err.println("doctors.txt not found in resources folder!");
                return;
            }
            
            // this is kind of like stringstream to check for the coma
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length == 4) {
                	// simple insert
                    Doctor doctor = new Doctor(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                    );
                    masterData.add(doctor);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setup columns
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Gmail.setCellValueFactory(new PropertyValueFactory<>("gmail"));
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
        doctorTable.setItems(sortedData);
    }
    
    public void switchToScene1(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/AppointmentOne_1.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchToScene2(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("/com/medisys/AppointmentOne_2.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	@FXML
	private void handleButtonClick(javafx.event.ActionEvent event) {
	    Button clicked = (Button) event.getSource();

	    if (clicked == buttonA) {
	        setActive(buttonA);
	        setInactive(buttonB);
	        animatePop(buttonA);

	        // Sort by Name
	        sortedData.setComparator((d1, d2) -> d1.getName().compareToIgnoreCase(d2.getName()));

	    } else if (clicked == buttonB) {
	        setActive(buttonB);
	        setInactive(buttonA);
	        animatePop(buttonB);

	        // Sort by Faculty
	        sortedData.setComparator((d1, d2) -> d1.getFaculty().compareToIgnoreCase(d2.getFaculty()));
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
}
