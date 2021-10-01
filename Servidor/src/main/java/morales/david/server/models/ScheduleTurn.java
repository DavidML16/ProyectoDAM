package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class ScheduleTurn {

    private Classroom classroom;
    private Teacher teacher;
    private Course course;

    public ScheduleTurn() {
        this(null, null, null);
    }

    public ScheduleTurn(Classroom classroom, Teacher teacher, Course course) {
        this.classroom = classroom;
        this.teacher = teacher;
        this.course = course;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public static ScheduleTurn parse(LinkedTreeMap scheduleMap) {

        ScheduleTurn scheduleTurn = new ScheduleTurn();

        LinkedTreeMap classroomMap = (LinkedTreeMap) scheduleMap.get("classroom");
        if(classroomMap != null) {
            Classroom classroom = Classroom.parse(classroomMap);
            scheduleTurn.setClassroom(classroom);
        }

        LinkedTreeMap teacherMap = (LinkedTreeMap) scheduleMap.get("teacher");
        if(teacherMap != null) {
            Teacher teacher = Teacher.parse(teacherMap);
            scheduleTurn.setTeacher(teacher);
        }

        LinkedTreeMap courseMap = (LinkedTreeMap) scheduleMap.get("course");
        if(courseMap != null) {
            Course course = Course.parse(courseMap);
            scheduleTurn.setCourse(course);
        }

        return scheduleTurn;

    }

    @Override
    public String toString() {
        return "ScheduleTurn{" +
                "classroom=" + classroom +
                ", teacher=" + teacher +
                ", course=" + course +
                '}';
    }

}
