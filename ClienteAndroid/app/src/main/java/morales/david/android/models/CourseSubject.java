package morales.david.android.models;

public class CourseSubject {

    private Course course;
    private Subject subject;

    /**
     * Create a new instance of CourseSubject with given params
     * @param course
     * @param subject
     */
    public CourseSubject(Course course, Subject subject) {
        this.course = course;
        this.subject = subject;
    }

    /**
     * Get course
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Get subject
     * @return subject
     */
    public Subject getSubject() {
        return subject;
    }

}
