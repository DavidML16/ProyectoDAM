package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

public class Schedule {

    private int id;
    private Teacher teacher;
    private Subject subject;
    private Group group;
    private Classroom classroom;
    private TimeZone timeZone;

    public Schedule() {
        this(-1, null, null, null, null, null);
    }

    public Schedule(int id, Teacher teacher, Subject subject, Group group, Classroom classroom, TimeZone timeZone) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.classroom = classroom;
        this.timeZone = timeZone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public static Schedule parse(LinkedTreeMap scheduleMap) {

        int id = ((Double) scheduleMap.get("id")).intValue();

        LinkedTreeMap teacherMap = (LinkedTreeMap) scheduleMap.get("teacher");
        Teacher teacher = Teacher.parse(teacherMap);

        LinkedTreeMap subjectMap = (LinkedTreeMap) scheduleMap.get("subject");
        Subject subject = Subject.parse(subjectMap);

        LinkedTreeMap groupMap = (LinkedTreeMap) scheduleMap.get("group");
        Group group = Group.parse(groupMap);

        LinkedTreeMap classroomMap = (LinkedTreeMap) scheduleMap.get("classroom");
        Classroom classroom = Classroom.parse(classroomMap);

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) scheduleMap.get("timeZone");
        TimeZone timeZone = TimeZone.parse(timeZoneMap);

        return new Schedule(id, teacher, subject, group, classroom, timeZone);

    }

    public Schedule duplicate() {
        return new Schedule(id, teacher, subject, group, classroom, timeZone);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", teacher=" + teacher +
                ", subject=" + subject +
                ", group=" + group +
                ", classroom=" + classroom +
                ", timeZone=" + timeZone +
                '}';
    }

}
