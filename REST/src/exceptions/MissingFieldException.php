<?php

namespace Exceptions;

class MissingFieldException extends RestException {

    private $missing_field = '';


    /**
     * Get the missing field name
     *
     * @return  string      field name
     */
    public function getMissingField() {
        return $this->missing_field;
    }


    /**
     * Set the missing name name
     *
     * @param   $field      string      field name
     */
    public function setMissingField($field) {
        $this->missing_field = $field;
    }
}