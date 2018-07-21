<?php

namespace Exceptions;

class AuthenticationException extends RestException {

	/**
	 * Constructor
	 *
	 * @param	string     	$errorCode			error code
	 */
	public function __construct($errorCode) {
		parent::__construct(self::UNAUTHORIZED, $errorCode);
	}


	/**
	 * Add exception specific data
	 *
	 * @param	array		$data		base level data
	 */
	protected function addErrorData(array &$data) {

	}

}
