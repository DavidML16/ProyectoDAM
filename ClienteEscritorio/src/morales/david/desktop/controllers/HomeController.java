package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.models.*;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable, Controller {

    @FXML
    private Label teachersLabel;

    @FXML
    private Label coursesLabel;

    @FXML
    private Label groupsLabel;

    @FXML
    private Label subjectsLabel;

    @FXML
    private Label classroomsLabel;

    @FXML
    private Label schedulesLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if(!Constants.FIRST_HOME_VIEW) {
            Platform.runLater(() -> {
                teachersLabel.setText(Integer.toString(DataManager.getInstance().getTeachers().size()));
                coursesLabel.setText(Integer.toString(DataManager.getInstance().getCourses().size()));
                groupsLabel.setText(Integer.toString(DataManager.getInstance().getGroups().size()));
                subjectsLabel.setText(Integer.toString(DataManager.getInstance().getSubjects().size()));
                classroomsLabel.setText(Integer.toString(DataManager.getInstance().getClassrooms().size()));
                schedulesLabel.setText(Integer.toString(DataManager.getInstance().getSchedules().size()));
            });
        }

        Constants.FIRST_HOME_VIEW = false;

        DataManager.getInstance().getTeachers().addListener((ListChangeListener<Teacher>) c -> {
            Platform.runLater(() -> {
                teachersLabel.setText(Integer.toString(DataManager.getInstance().getTeachers().size()));
            });
        });

        DataManager.getInstance().getCourses().addListener((ListChangeListener<Course>) c -> {
            Platform.runLater(() -> {
                coursesLabel.setText(Integer.toString(DataManager.getInstance().getCourses().size()));
            });
        });

        DataManager.getInstance().getGroups().addListener((ListChangeListener<Group>) c -> {
            Platform.runLater(() -> {
                groupsLabel.setText(Integer.toString(DataManager.getInstance().getGroups().size()));
            });
        });

        DataManager.getInstance().getSubjects().addListener((ListChangeListener<Subject>) c -> {
            Platform.runLater(() -> {
                subjectsLabel.setText(Integer.toString(DataManager.getInstance().getSubjects().size()));
            });
        });

        DataManager.getInstance().getClassrooms().addListener((ListChangeListener<Classroom>) c -> {
            Platform.runLater(() -> {
                classroomsLabel.setText(Integer.toString(DataManager.getInstance().getClassrooms().size()));
            });
        });

        DataManager.getInstance().getSchedules().addListener((ListChangeListener<Schedule>) c -> {
            Platform.runLater(() -> {
                schedulesLabel.setText(Integer.toString(DataManager.getInstance().getSchedules().size()));
            });
        });

    }

}
