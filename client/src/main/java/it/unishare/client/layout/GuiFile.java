package it.unishare.client.layout;

import it.unishare.common.connection.kademlia.KademliaFile;
import javafx.beans.property.*;

public class GuiFile {

    private StringProperty university;
    private StringProperty course;
    private StringProperty teacher;
    private StringProperty title;


    /**
     * Constructor
     */
    public GuiFile(KademliaFile file) {
        this.university = new SimpleStringProperty(file.getData().getUniversity());
        this.course = new SimpleStringProperty(file.getData().getCourse());
        this.teacher = new SimpleStringProperty(file.getData().getTeacher());
        this.title = new SimpleStringProperty(file.getData().getTitle());
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


    /**
     * Get title property
     *
     * @return  title property
     */
    public StringProperty titleProperty() {
        return title;
    }

}
