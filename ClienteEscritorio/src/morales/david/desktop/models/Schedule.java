package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.UUID;

public class Schedule {

    private String uuid;
    private Teacher teacher;
    private Subject subject;
    private Group group;
    private Classroom classroom;
    private TimeZone timeZone;

    public Schedule() {
        this(null, null, null, null, null);
    }

    public Schedule(Teacher teacher, Subject subject, Group group, Classroom classroom, TimeZone timeZone) {
        this(UUID.randomUUID().toString(), teacher, subject, group, classroom, timeZone);
    }

    public Schedule(String uuid, Teacher teacher, Subject subject, Group group, Classroom classroom, TimeZone timeZone) {
        this.uuid = uuid;
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.classroom = classroom;
        this.timeZone = timeZone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

        String uuid = (String) scheduleMap.get("uuid");

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

        return new Schedule(uuid, teacher, subject, group, classroom, timeZone);

    }

    @Override
    public String toString() {
        return "Schedule{" +
                "uuid='" + uuid + '\'' +
                ", teacher=" + teacher +
                ", subject=" + subject +
                ", group=" + group +
                ", classroom=" + classroom +
                ", timeZone=" + timeZone +
                '}';
    }

    public Schedule duplicate() {
        return new Schedule(teacher, subject, group, classroom, timeZone);
    }

}
