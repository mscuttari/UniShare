package it.unishare.common.connection.kademlia;

import java.io.Serializable;

public class KademliaFileData implements Serializable {

    // Serialization
    private static final long serialVersionUID = 5975688018355674770L;

    private String university;
    private String course;
    private String teacher;
    private String title;


    /**
     * Constructor
     *
     * @param   university      university name
     * @param   course          course name
     * @param   teacher         teacher name
     * @param   title           note title
     */
    public KademliaFileData(String university, String course, String teacher, String title) {
        this.university = university;
        this.course = course;
        this.teacher = teacher;
        this.title = title;
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


    /**
     * Get note title
     *
     * @return  note title
     */
    public String getTitle() {
        return title;
    }

}
