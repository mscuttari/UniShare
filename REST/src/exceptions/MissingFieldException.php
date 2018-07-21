<?php

namespace Exceptions;

class MissingFieldException extends RestException {

	private $missingField;


	/**
	 * Constructor
	 *
	 * @param	string     	$missingField		missing field name
	 */
	public function __construct(string $missingField) {
		parent::__construct(self::BAD_REQUEST, "MISSING_FIELD");

		$this->missingField = $missingField;
	}


	/**
	 * Get missing field name
	 *
	 * @return	string		missing field name
	 */
	public function getMissingField() : string {
		return $this->missingField;
	}


	/**
	 * Add exception specific data
	 *
	 * @param	array		$data		base level data
	 */
	protected function addErrorData(array &$data) {
		$data["field"] = $this->getMissingField();
	}

}