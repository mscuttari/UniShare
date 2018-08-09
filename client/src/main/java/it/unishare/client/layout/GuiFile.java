package it.unishare.client.layout;

import it.unishare.common.connection.kademlia.KademliaFile;
import javafx.beans.property.*;

public class GuiFile {

    private KademliaFile file;

    private StringProperty author;
    private StringProperty title;
    private StringProperty university;
    private StringProperty department;
    private StringProperty course;
    private StringProperty teacher;


    /**
     * Constructor
     */
    public GuiFile(KademliaFile file) {
        this.file = file;

        this.author = new SimpleStringProperty(file.getData().getAuthor());
        this.title = new SimpleStringProperty(file.getData().getTitle());
        this.university = new SimpleStringProperty(file.getData().getUniversity());
        this.department = new SimpleStringProperty(file.getData().getDepartment());
        this.course = new SimpleStringProperty(file.getData().getCourse());
        this.teacher = new SimpleStringProperty(file.getData().getTeacher());
    }


    /**
     * Get file
     *
     * @return  file
     */
    public KademliaFile getFile() {
        return file;
    }


    /**
     * Get author property
     *
     * @return  author property
     */
    public StringProperty authorProperty() {
        return author;
    }


    /**
     * Get title property
     *
     * @return  title property
     */
    public StringProperty titleProperty() {
        return title;
    }


    /**
     * Get university property
     *
     * @return  school property
     */
    public StringProperty universityProperty() {
        return university;
    }


    /**
     * Get department property
     *
     * @return  department property
     */
    public StringProperty departmentProperty() {
        return department;
    }


    /**
     * Get course property
     *
     * @return  course property
     */
    public StringProperty courseProperty() {
        return course;
    }


    /**
     * Get teacher property
     *
     * @return  teacher property
     */
    public StringProperty teacherProperty() {
        return teacher;
    }

}
