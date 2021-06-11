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

        Schedule schedule = new Schedule();

        String uuid = (String) scheduleMap.get("uuid");
        schedule.setUuid(uuid);

        LinkedTreeMap teacherMap = (LinkedTreeMap) scheduleMap.get("teacher");
        if(teacherMap != null) {
            Teacher teacher = Teacher.parse(teacherMap);
            schedule.setTeacher(teacher);
        }

        LinkedTreeMap subjectMap = (LinkedTreeMap) scheduleMap.get("subject");
        if(subjectMap != null) {
            Subject subject = Subject.parse(subjectMap);
            schedule.setSubject(subject);
        }

        LinkedTreeMap groupMap = (LinkedTreeMap) scheduleMap.get("group");
        if(groupMap != null) {
            Group group = Group.parse(groupMap);
            schedule.setGroup(group);
        }

        LinkedTreeMap classroomMap = (LinkedTreeMap) scheduleMap.get("classroom");
        if(classroomMap != null) {
            Classroom classroom = Classroom.parse(classroomMap);
            schedule.setClassroom(classroom);
        }

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) scheduleMap.get("timeZone");
        TimeZone timeZone = TimeZone.parse(timeZoneMap);
        schedule.setTimeZone(timeZone);

        return schedule;

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

    public Schedule duplicateUUID() {
        return new Schedule(uuid, teacher, subject, group, classroom, timeZone);
    }

    public String getText(int type, boolean plain) {

        if(teacher == null || subject == null || timeZone == null)
            return "";

        StringBuilder sb = new StringBuilder();

        if(type == 0) {

            sb.append(teacher.getAbreviation());

            if(!plain)
                sb.append("\n");
            else
                sb.append("   ");

            if(classroom != null) {
                sb.append(subject.getAbreviation() + "   " + classroom.toString());
            } else {
                sb.append(subject.getName());
            }

            if(!plain)
                sb.append("\n");
            else
                sb.append("   ");

            if(group != null) {
                sb.append(group);
            }

        } else if(type == 1) {

            if(classroom != null) {

                if(group != null) {
                    sb.append(subject.getAbreviation() + "   " + classroom.toString());
                } else {
                    sb.append(subject.getName());
                }

            } else {
                sb.append(subject.getName());
            }

            if(!plain)
                sb.append("\n");
            else
                sb.append("   ");

            if(group != null) {
                sb.append(group);
            }

        } else if(type == 2) {

            sb.append(teacher.getAbreviation());

            if(!plain)
                sb.append("\n");
            else
                sb.append("   ");

            if(classroom != null) {
                sb.append(subject.getAbreviation() + "   " + classroom.toString());
            } else {
                sb.append(subject.getName());
            }

        }

        return sb.toString();

    }

}
