package morales.david.desktop.controllers.classrooms;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerGUI;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ExportEmptyClassroomsManager;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.SchedulerItem;
import morales.david.desktop.models.TimeZone;
import morales.david.desktop.utils.Constants;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmptyClassroomsTimezoneController implements Initializable, Controller {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Label infoLabel;

    @FXML
    private Button exportButton;

    @FXML
    private TextField filterField;

    private TimeZone timeZone;
    private List<Classroom> classrooms;
    private List<Classroom> filteredClassrooms;

    private GridPane classroomsGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        classroomsGrid = new GridPane();
        classroomsGrid.setVgap(Constants.GAP_SIZE);
        classroomsGrid.setHgap(Constants.GAP_SIZE);

        anchorPane.setRightAnchor(scrollPane, Constants.GAP_SIZE * 4);
        anchorPane.setLeftAnchor(scrollPane, Constants.GAP_SIZE * 4);

    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void init(TimeZone timeZone, List<Classroom> classrooms) {

        this.classrooms = classrooms;
        this.filteredClassrooms = new ArrayList<>(classrooms);
        this.timeZone = timeZone;

        classroomsGrid.setVgap(Constants.GAP_SIZE * 4);
        classroomsGrid.setHgap(Constants.GAP_SIZE * 4);

        classroomsGrid.setPadding(new Insets(10));

        infoLabel.setText("Aulas libres para el " + timeZone);

        populateGrid();

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                filteredClassrooms = new ArrayList<>(classrooms);
                populateGrid();
                return;
            }

            List<Classroom> items = new ArrayList<>();

            for(Classroom classroom : classrooms) {
                if(classroom.getName().contains(filterField.textProperty().get().toUpperCase()))
                    items.add(classroom);
            }

            filteredClassrooms = items;

            populateGrid();

        });

    }

    private void populateGrid() {

        classroomsGrid.getChildren().clear();

        int row = 0, column = 0;
        for(int i = 0; i < filteredClassrooms.size(); i++) {

            Classroom classroom = filteredClassrooms.get(i);

            JFXButton button = new JFXButton();

            button.setMinSize(100, 40);
            button.setPrefSize(500, 75);
            button.setMaxHeight(75);

            GridPane.setHgrow(button, Priority.ALWAYS);
            GridPane.setFillWidth(button, true);

            button.setText(classroom.getName());

            button.setStyle("-fx-background-color: #FFFFFF; -fx-font-size: 18");

            classroomsGrid.add(button, column, row, 1, 1);

            column++;
            if(column > 5) {
                column = 0;
                row++;
            }

        }

        scrollPane.setContent(classroomsGrid);

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == exportButton) {

            try {
                ExportEmptyClassroomsManager.getInstance().exportTimeZone(timeZone, classrooms);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

    }

}
