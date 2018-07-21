<?php

namespace Exceptions;

class MethodNotAllowedException extends RestException {

	/**
	 * Constructor
	 */
	public function __construct() {
		parent::__construct(self::METHOD_NOT_ALLOWED);
	}


	/**
	 * Add exception specific data
	 *
	 * @param	array		$data		base level data
	 */
	protected function addErrorData(array &$data) {

	}

}
