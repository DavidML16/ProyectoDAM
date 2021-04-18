package morales.david.desktop.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.models.*;

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

    public synchronized ObservableList<Teacher> getTeachers() {
        return teachers;
    }

    public synchronized void setTeachers(ObservableList<Teacher> teachers) {
        this.teachers = teachers;
    }


    private ObservableList<Credential> credentials = FXCollections.observableArrayList();

    public synchronized ObservableList<Credential> getCredentials() {
        return credentials;
    }

    public synchronized void setCredentials(ObservableList<Credential> credentials) {
        this.credentials = credentials;
    }


    private ObservableList<Classroom> classrooms = FXCollections.observableArrayList();

    public synchronized ObservableList<Classroom> getClassrooms() {
        return classrooms;
    }

    public synchronized void setClassrooms(ObservableList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }


    private ObservableList<Course> courses = FXCollections.observableArrayList();

    public synchronized ObservableList<Course> getCourses() {
        return courses;
    }

    public synchronized void setCourses(ObservableList<Course> courses) {
        this.courses = courses;
    }


    private ObservableList<Subject> subjects = FXCollections.observableArrayList();

    public synchronized ObservableList<Subject> getSubjects() {
        return subjects;
    }

    public synchronized void setSubjects(ObservableList<Subject> subjects) {
        this.subjects = subjects;
    }

}
