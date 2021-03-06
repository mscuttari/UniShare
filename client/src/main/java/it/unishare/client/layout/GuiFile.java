package it.unishare.client.layout;

import it.unishare.common.connection.dht.NoteFile;
import javafx.beans.property.*;

public class GuiFile {

    private NoteFile file;

    private StringProperty title;
    private StringProperty author;
    private StringProperty university;
    private StringProperty department;
    private StringProperty course;
    private StringProperty teacher;


    /**
     * Constructor
     */
    public GuiFile(NoteFile file) {
        this.file = file;

        this.title = new SimpleStringProperty(file.getData().getTitle());
        this.author = new SimpleStringProperty(file.getData().getAuthor());
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
    public NoteFile getFile() {
        return file;
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
     * Get author property
     *
     * @return  author property
     */
    public StringProperty authorProperty() {
        return author;
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
