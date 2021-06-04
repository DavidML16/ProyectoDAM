package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.UUID;

public class Schedule {

    private String uuid;
    private Teacher teacher;
    private Subject subject;
    private Group group;
    private Classroom classroom;
    private TimeZone timeZone;

    /**
     * Empty constructor of Schedule
     */
    public Schedule() {
        this(null, null, null, null, null);
    }

    /**
     * Create a new instance of Schedule with given params, and a random UUID
     * @param teacher
     * @param subject
     * @param group
     * @param classroom
     * @param timeZone
     */
    public Schedule(Teacher teacher, Subject subject, Group group, Classroom classroom, TimeZone timeZone) {
        this(UUID.randomUUID().toString(), teacher, subject, group, classroom, timeZone);
    }

    /**
     * Create a new instance of Schedule with given params
     * @param uuid
     * @param teacher
     * @param subject
     * @param group
     * @param classroom
     * @param timeZone
     */
    public Schedule(String uuid, Teacher teacher, Subject subject, Group group, Classroom classroom, TimeZone timeZone) {
        this.uuid = uuid;
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.classroom = classroom;
        this.timeZone = timeZone;
    }

    /**
     * Get uuid of the schedule
     * @return
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid of the schedule
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get associated teacher of the schedule
     * @return teacher
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Set associated teacher of the shechedule
     * @param teacher
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    /**
     * Get associated subject of the schedule
     * @return subject
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Set associated subject of the schedule
     * @param subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Get associated group of the schedule
     * @return group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Set associated group of the schedule
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Get associated classroom of the schedule
     * @return classroom
     */
    public Classroom getClassroom() {
        return classroom;
    }

    /**
     * Set associated classroom of the schedule
     * @param classroom
     */
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    /**
     * Get associated timeZone of the schedule
     * @return timeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Set associated timeZone of the schedule
     * @param timeZone
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Convert the received TreeMap to a Schedule object
     * @param scheduleMap
     * @return schedule object
     */
    public static Schedule parse(LinkedTreeMap scheduleMap) {

        String uuid = (String) scheduleMap.get("uuid");

        LinkedTreeMap teacherMap = (LinkedTreeMap) scheduleMap.get("teacher");
        if(teacherMap == null)
            return null;
        Teacher teacher = Teacher.parse(teacherMap);

        LinkedTreeMap subjectMap = (LinkedTreeMap) scheduleMap.get("subject");
        if(subjectMap == null)
            return null;
        Subject subject = Subject.parse(subjectMap);

        LinkedTreeMap groupMap = (LinkedTreeMap) scheduleMap.get("group");
        if(groupMap == null)
            return null;
        Group group = Group.parse(groupMap);

        LinkedTreeMap classroomMap = (LinkedTreeMap) scheduleMap.get("classroom");
        if(classroomMap == null)
            return null;
        Classroom classroom = Classroom.parse(classroomMap);

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) scheduleMap.get("timeZone");
        if(timeZoneMap == null)
            return null;
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

}
