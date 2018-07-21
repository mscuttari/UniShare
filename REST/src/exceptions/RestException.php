<?php

namespace Exceptions;

use Exception;

abstract class RestException extends Exception {

	const BAD_REQUEST			= "HTTP/1.0 400 Bad Request";
	const UNAUTHORIZED 			= "HTTP/1.0 401 Unauthorized";
	const FORBIDDEN 			= "HTTP/1.0 403 Forbidden";
	const NOT_FOUND 			= "HTTP/1.0 404 Not Found";
	const METHOD_NOT_ALLOWED 	= "HTTP/1.0 405 Method Not Allowed";
	const INTERNAL_SERVER_ERROR	= "HTTP/1.0 500 Internal Server Error";
	const SERVICE_UNAVAILABLE	= "HTTP/1.0 503 Service Unavailable";


	private $httpResponseCode;
	private $errorCode;


	/**
	 * Constructor
	 *
	 * @param	string		$httpResponseCode	HTTP response code
	 * @param	string     	$errorCode			error code
	 */
	public function __construct(string $httpResponseCode, string $errorCode = null) {
		$this->httpResponseCode = $httpResponseCode;
		$this->errorCode = $errorCode;
	}


	/**
	 * Get HTTP response code
	 *
	 * @return 	string		HTTP response code
	 */
	public function getHttpResponseCode(): string {
		return $this->httpResponseCode;
	}


	/**
	 * Get error code
	 *
	 * @return	string		error code
	 */
	public function getErrorCode() : string {
		return $this->errorCode == null || empty($this->errorCode) ? "" : $this->errorCode;
	}


	/**
	 * Get exception data to be used for the HTTP response
	 *
	 * @return 	array		data
	 */
	public function getErrorData() : array {
		$data = array();

		if ($this->getErrorCode() != "")
			$data["code"] = $this->getErrorCode();

		$this->addErrorData($data);

		return $data;
	}


	/**
	 * Add exception specific data
	 *
	 * @param 	array	$data		base level data
	 */
	protected abstract function addErrorData(array &$data);

}