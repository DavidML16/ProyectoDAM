package morales.david.android.managers;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import morales.david.android.models.Classroom;
import morales.david.android.models.Course;
import morales.david.android.models.Credential;
import morales.david.android.models.Day;
import morales.david.android.models.Group;
import morales.david.android.models.Hour;
import morales.david.android.models.Subject;
import morales.david.android.models.Teacher;
import morales.david.android.models.TimeZone;

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
    private MutableLiveData<List<Group>> groups;
    private MutableLiveData<List<Subject>> subjects;
    private MutableLiveData<List<Day>> days;
    private MutableLiveData<List<Hour>> hours;
    private MutableLiveData<List<TimeZone>> timeZones;


    public synchronized MutableLiveData<List<Teacher>> getTeachers() {
        if (teachers == null) {
            teachers = new MutableLiveData<>();
        }
        return teachers;
    }

    public synchronized void setTeachers(List<Teacher> teachers) {
        if (this.teachers == null) {
            this.teachers = new MutableLiveData<>();
        }
        this.teachers.setValue(teachers);
    }


    public synchronized MutableLiveData<List<Credential>> getCredentials() {
        if (credentials == null) {
            credentials = new MutableLiveData<>();
        }
        return credentials;
    }

    public synchronized void setCredentials(List<Credential> credentials) {
        if (this.credentials == null) {
            this.credentials = new MutableLiveData<>();
        }
        this.credentials.setValue(credentials);
    }


    public synchronized MutableLiveData<List<Classroom>> getClassrooms() {
        if (classrooms == null) {
            classrooms = new MutableLiveData<>();
        }
        return classrooms;
    }

    public synchronized void setClassrooms(List<Classroom> classrooms) {
        if (this.classrooms == null) {
            this.classrooms = new MutableLiveData<>();
        }
        this.classrooms.setValue(classrooms);
    }


    public synchronized MutableLiveData<List<Course>> getCourses() {
        if (courses == null) {
            courses = new MutableLiveData<>();
        }
        return courses;
    }

    public synchronized void setCourses(List<Course> courses) {
        if (this.courses == null) {
            this.courses = new MutableLiveData<>();
        }
        this.courses.setValue(courses);
    }


    public synchronized MutableLiveData<List<Group>> getGroups() {
        if (groups == null) {
            groups = new MutableLiveData<>();
        }
        return groups;
    }

    public synchronized void setGroups(List<Group> groups) {
        if (this.groups == null) {
            this.groups = new MutableLiveData<>();
        }
        this.groups.setValue(groups);
    }


    public synchronized MutableLiveData<List<Subject>> getSubjects() {
        if (subjects == null) {
            subjects = new MutableLiveData<>();
        }
        return subjects;
    }

    public synchronized void setSubjects(List<Subject> subjects) {
        if (this.subjects == null) {
            this.subjects = new MutableLiveData<>();
        }
        this.subjects.setValue(subjects);
    }


    public synchronized MutableLiveData<List<Day>> getDays() {
        if (days == null) {
            days = new MutableLiveData<>();
        }
        return days;
    }

    public synchronized void setDays(List<Day> days) {
        if (this.days == null) {
            this.days = new MutableLiveData<>();
        }
        this.days.setValue(days);
    }


    public synchronized MutableLiveData<List<Hour>> getHours() {
        if (hours == null) {
            hours = new MutableLiveData<>();
        }
        return hours;
    }

    public synchronized void setHours(List<Hour> hours) {
        if (this.hours == null) {
            this.hours = new MutableLiveData<>();
        }
        this.hours.setValue(hours);
    }


    public synchronized MutableLiveData<List<TimeZone>> getTimeZones() {
        if (timeZones == null) {
            timeZones = new MutableLiveData<>();
        }
        return timeZones;
    }

    public synchronized void setTimeZones(List<TimeZone> timeZones) {
        if (this.timeZones == null) {
            this.timeZones = new MutableLiveData<>();
        }
        this.timeZones.setValue(timeZones);
    }


}
