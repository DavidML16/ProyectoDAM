package morales.david.server.models;

public class CourseSubject {

    private Course course;
    private Subject subject;

    public CourseSubject(Course course, Subject subject) {
        this.course = course;
        this.subject = subject;
    }

    public Course getCourse() {
        return course;
    }

    public Subject getSubject() {
        return subject;
    }

}
