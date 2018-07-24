package it.unishare.common.exceptions;

public class MissingFieldException extends Exception {

    private static final long serialVersionUID = -5085624064289030336L;
    private String missingField;


    /**
     * Constructor
     *
     * @param   missingField        missing field name
     */
    public MissingFieldException(String missingField) {
        this.missingField = missingField;
    }


    /**
     * Get missing field name
     *
     * @return  missing field name
     */
    public String getMissingField() {
        return missingField;
    }

}
