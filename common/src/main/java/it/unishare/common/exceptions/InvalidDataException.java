package it.unishare.common.exceptions;

public class InvalidDataException extends Exception {

    private static final long serialVersionUID = -7799565530950276949L;
    private String invalidField;


    /**
     * Constructor
     *
     * @param   invalidField        invalid field name
     */
    public InvalidDataException(String invalidField) {
        this.invalidField = invalidField;
    }


    /**
     * Get invalid field name
     *
     * @return  invalid field name
     */
    public String getInvalidField() {
        return invalidField;
    }

}
