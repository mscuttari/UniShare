package it.unishare.common.connection.kademlia;

import java.io.Serializable;

public class KademliaFileData implements Serializable {

    // Serialization
    private static final long serialVersionUID = 5975688018355674770L;

    private String university;
    private String department;
    private String course;
    private String teacher;
    private String title;


    /**
     * Constructor
     *
     * @param   title           note title
     * @param   university      university name
     * @param   department      department name
     * @param   course          course name
     * @param   teacher         teacher name
     */
    public KademliaFileData(String title, String university, String department, String course, String teacher) {
        setTitle(title);
        setUniversity(university);
        setDepartment(department);
        setCourse(course);
        setTeacher(teacher);
    }


    @Override
    public String toString() {
        return "{" + title + ", " + university + ", " + department + ", " + course + ", " + teacher + "}";
    }


    /**
     * Get note title
     *
     * @return  note title
     */
    public String getTitle() {
        return title;
    }


    /**
     * Set note title
     *
     * @param   title   note title
     */
    private void setTitle(String title) {
        this.title = title == null || title.isEmpty() ? null : title;
    }


    /**
     * Get university name
     *
     * @return  university name
     */
    public String getUniversity() {
        return university;
    }


    /**
     * Set university name
     *
     * @param   university      university name
     */
    private void setUniversity(String university) {
        this.university = university == null || university.isEmpty() ? null : university;
    }


    /**
     * Get department name
     *
     * @return  department name
     */
    public String getDepartment() {
        return department;
    }


    /**
     * Set department name
     *
     * @param   department      department name
     */
    private void setDepartment(String department) {
        this.department = department == null || department.isEmpty() ? null : department;
    }


    /**
     * Get course name
     *
     * @return  course name
     */
    public String getCourse() {
        return course;
    }


    /**
     * Set course name
     *
     * @param   course      course name
     */
    private void setCourse(String course) {
        this.course = course == null || course.isEmpty() ? null : course;
    }


    /**
     * Get teacher name
     *
     * @return  taecher name
     */
    public String getTeacher() {
        return teacher;
    }


    /**
     * Set teacher name
     *
     * @param   teacher     teacher name
     */
    private void setTeacher(String teacher) {
        this.teacher = teacher == null || teacher.isEmpty() ? null : teacher;
    }

}
