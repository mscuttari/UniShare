<?php

namespace Exceptions;

class InvalidDataException extends RestException {

	private $invalidField;


	/**
	 * Constructor
	 *
	 * @param	string     	$invalidField	invalid field name
	 */
	public function __construct(string $invalidField) {
		parent::__construct(self::BAD_REQUEST, "INVALID_DATA");

		$this->invalidField = $invalidField;
	}


	/**
	 * Get invalid field name
	 *
	 * @return	string		invalid field name
	 */
	public function getInvalidField() : string {
		return $this->invalidField;
	}


	/**
	 * Add exception specific data
	 *
	 * @param	array		$data		base level data
	 */
	protected function addErrorData(array &$data) {
		$data["field"] = $this->getInvalidField();
	}

}