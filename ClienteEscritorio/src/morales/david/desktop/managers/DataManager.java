package morales.david.desktop.managers;

import javafx.beans.value.ObservableIntegerValue;
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


    private ObservableList<Group> groups = FXCollections.observableArrayList();

    public synchronized ObservableList<Group> getGroups() {
        return groups;
    }

    public synchronized void setGroups(ObservableList<Group> groups) {
        this.groups = groups;
    }


    private ObservableList<Subject> subjects = FXCollections.observableArrayList();

    public synchronized ObservableList<Subject> getSubjects() {
        return subjects;
    }

    public synchronized void setSubjects(ObservableList<Subject> subjects) {
        this.subjects = subjects;
    }


    private ObservableList<Day> days = FXCollections.observableArrayList();

    public synchronized ObservableList<Day> getDays() {
        return days;
    }

    public synchronized void setDays(ObservableList<Day> days) {
        this.days = days;
    }


    private ObservableList<Hour> hours = FXCollections.observableArrayList();

    public synchronized ObservableList<Hour> getHours() {
        return hours;
    }

    public synchronized void setHours(ObservableList<Hour> hours) {
        this.hours = hours;
    }


    private ObservableList<TimeZone> timeZones = FXCollections.observableArrayList();

    public synchronized ObservableList<TimeZone> getTimeZones() {
        return timeZones;
    }

    public synchronized void setTimeZones(ObservableList<TimeZone> timeZones) {
        this.timeZones = timeZones;
    }


    private ObservableList<Integer> schedules = FXCollections.observableArrayList();

    public synchronized ObservableList<Integer> getSchedules() {
        return schedules;
    }

    public synchronized void setSchedules(ObservableList<Integer> schedules) {
        this.schedules = schedules;
    }

}
