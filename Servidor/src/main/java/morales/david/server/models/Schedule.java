package morales.david.server.models;

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

}
