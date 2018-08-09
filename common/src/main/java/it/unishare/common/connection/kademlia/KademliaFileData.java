package it.unishare.common.connection.kademlia;

import java.io.Serializable;

public class KademliaFileData implements Serializable {

    // Serialization
    private static final long serialVersionUID = 5975688018355674770L;

    private String author;
    private String title;
    private String university;
    private String department;
    private String course;
    private String teacher;


    /**
     * Constructor
     *
     * @param   title           note title
     * @param   author          author name
     * @param   university      university name
     * @param   department      department name
     * @param   course          course name
     * @param   teacher         teacher name
     */
    public KademliaFileData(String title, String author, String university, String department, String course, String teacher) {
        setTitle(title);
        setAuthor(author);
        setUniversity(university);
        setDepartment(department);
        setCourse(course);
        setTeacher(teacher);
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KademliaFileData))
            return false;

        KademliaFileData data = (KademliaFileData) obj;

        if (getTitle() == null && data.getTitle() != null) return false;
        if (!getTitle().equals(data.getTitle())) return false;

        if (getAuthor() == null && data.getAuthor() != null) return false;
        if (!getAuthor().equals(data.getAuthor())) return false;

        if (getUniversity() == null && data.getUniversity() != null) return false;
        if (!getUniversity().equals(data.getUniversity())) return false;

        if (getDepartment() == null && data.getDepartment() != null) return false;
        if (!getDepartment().equals(data.getDepartment())) return false;

        if (getCourse() == null && data.getCourse() != null) return false;
        if (!getCourse().equals(data.getCourse())) return false;

        if (getTeacher() == null && data.getTeacher() != null) return false;
        if (!getTeacher().equals(data.getTeacher())) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return title == null ? 57 : title.hashCode();
    }


    @Override
    public String toString() {
        return "{" + title + ", " + author + ", " + university + ", " + department + ", " + course + ", " + teacher + "}";
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
     * Get author name
     *
     * @return  author name
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Set author name
     *
     * @param   author      author name
     */
    public void setAuthor(String author) {
        this.author = author == null || author.isEmpty() ? null : author;
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
