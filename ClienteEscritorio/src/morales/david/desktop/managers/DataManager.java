package morales.david.desktop.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataManager {

    private static DataManager INSTANCE = null;

    public static DataManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new DataManager();
        return INSTANCE;
    }


    private ObservableList<Teacher> teachers = FXCollections.observableArrayList();

    public ObservableList<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(ObservableList<Teacher> teachers) {
        this.teachers = teachers;
    }


    private ObservableList<Classroom> classrooms = FXCollections.observableArrayList();

    public ObservableList<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(ObservableList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }


    private ObservableList<Course> courses = FXCollections.observableArrayList();

    public ObservableList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ObservableList<Course> courses) {
        this.courses = courses;
    }

}
