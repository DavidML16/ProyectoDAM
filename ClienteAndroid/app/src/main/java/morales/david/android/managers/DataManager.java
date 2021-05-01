package morales.david.android.managers;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import morales.david.android.models.*;

public final class DataManager {


    private static DataManager INSTANCE = null;

    public static DataManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new DataManager();
        return INSTANCE;
    }


    private MutableLiveData<List<Teacher>> teachers;
    private MutableLiveData<List<Credential>> credentials;
    private MutableLiveData<List<Classroom>> classrooms;
    private MutableLiveData<List<Course>> courses;
    private MutableLiveData<List<Subject>> subjects;
    private MutableLiveData<List<Day>> days;
    private MutableLiveData<List<Hour>> hours;


    public synchronized MutableLiveData<List<Teacher>> getTeachers() {
        if (teachers == null) {
            teachers = new MutableLiveData<>();
        }
        return teachers;
    }

    public synchronized void setTeachers(List<Teacher> teachers) {
        this.teachers.setValue(teachers);
    }


    public synchronized MutableLiveData<List<Credential>> getCredentials() {
        if (credentials == null) {
            credentials = new MutableLiveData<>();
        }
        return credentials;
    }

    public synchronized void setCredentials(List<Credential> credentials) {
        this.credentials.setValue(credentials);
    }


    public synchronized MutableLiveData<List<Classroom>> getClassrooms() {
        if (classrooms == null) {
            classrooms = new MutableLiveData<>();
        }
        return classrooms;
    }

    public synchronized void setClassrooms(List<Classroom> classrooms) {
        this.classrooms.setValue(classrooms);
    }


    public synchronized MutableLiveData<List<Course>> getCourses() {
        if (courses == null) {
            courses = new MutableLiveData<>();
        }
        return courses;
    }

    public synchronized void setCourses(List<Course> courses) {
        this.courses.setValue(courses);
    }


    public synchronized MutableLiveData<List<Subject>> getSubjects() {
        if (subjects == null) {
            subjects = new MutableLiveData<>();
        }
        return subjects;
    }

    public synchronized void setSubjects(List<Subject> subjects) {
        this.subjects.setValue(subjects);
    }


    public synchronized MutableLiveData<List<Day>> getDays() {
        if (days == null) {
            days = new MutableLiveData<>();
        }
        return days;
    }

    public synchronized void setDays(List<Day> days) {
        this.days.setValue(days);
    }


    public synchronized MutableLiveData<List<Hour>> getHours() {
        if (hours == null) {
            hours = new MutableLiveData<>();
        }
        return hours;
    }

    public synchronized void setHours(List<Hour> hours) {
        this.hours.setValue(hours);
    }


}
