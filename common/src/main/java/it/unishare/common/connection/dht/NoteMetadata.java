package it.unishare.common.connection.dht;

import it.unishare.common.kademlia.KademliaFileData;

public final class NoteMetadata extends KademliaFileData {

    // Serialization
    private static final long serialVersionUID = 5975688018355674770L;

    private String title;
    private String author;
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
    public NoteMetadata(String title, String author, String university, String department, String course, String teacher) {
        setTitle(title);
        setAuthor(author);
        setUniversity(university);
        setDepartment(department);
        setCourse(course);
        setTeacher(teacher);
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NoteMetadata))
            return false;

        NoteMetadata data = (NoteMetadata) obj;

        if (title == null && data.title != null) return false;
        if (title != null && !title.equals(data.title)) return false;

        if (author == null && data.author != null) return false;
        if (author != null && !author.equals(data.author)) return false;

        if (university == null && data.university != null) return false;
        if (university != null && !university.equals(data.university)) return false;

        if (department == null && data.department != null) return false;
        if (department != null && !department.equals(data.department)) return false;

        if (course == null && data.course != null) return false;
        if (course != null && !course.equals(data.course)) return false;

        if (teacher == null && data.teacher != null) return false;
        if (teacher != null && !teacher.equals(data.teacher)) return false;

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