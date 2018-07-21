<?php

namespace Exceptions;

class ServiceUnavailableException extends RestException {

	/**
	 * Constructor
	 */
	public function __construct() {
		parent::__construct(self::SERVICE_UNAVAILABLE);
	}


	/**
	 * Add exception specific data
	 *
	 * @param	array		$data		base level data
	 */
	protected function addErrorData(array &$data) {

	}

}
