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
        this.title = title;
        this.university = university;
        this.department = department;
        this.course = course;
        this.teacher = teacher;
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
     * Get university name
     *
     * @return  university name
     */
    public String getUniversity() {
        return university;
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
     * Get course name
     *
     * @return  course name
     */
    public String getCourse() {
        return course;
    }


    /**
     * Get teacher name
     *
     * @return  taecher name
     */
    public String getTeacher() {
        return teacher;
    }

}
