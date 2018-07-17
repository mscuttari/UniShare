<?php

namespace Exceptions;

class InvalidDataException extends RestException {

    private $invalid_field = '';


    /**
     * Get the invalid field name
     *
     * @return  string      field name
     */
    public function getInvalidField() {
        return $this->invalid_field;
    }


    /**
     * Set the invalid name name
     *
     * @param   $field      string      field name
     */
    public function setInvalidField($field) {
        $this->invalid_field = $field;
    }
}