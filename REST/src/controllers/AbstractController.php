<?php

namespace Controllers;

use Exceptions\RestException;
use Exceptions\ForbiddenException;
use Exceptions\InvalidCredentialsException;
use Exceptions\InvalidDataException;
use Exceptions\MissingFieldException;
use Exceptions\NotFoundException;
use Exceptions\UserNotFoundException;
use Models\AbstractModel;
use Models\User;

abstract class AbstractController {

	/* Response types */
	const TYPE_JSON  = 'json';
	const TYPE_IMAGE = 'image';

	/* User info */
	protected $user_id;
	private $authenticated = false;

	/* Model */
	private $model;
	protected $model_name;

	public function __construct() {
		if(isset($_SERVER['HTTP_API_KEY'])) {
			// API key authentication (header: api-key)
			$this->apiKeyAuthentication($_SERVER['HTTP_API_KEY']);

		} elseif (isset($_SERVER['PHP_AUTH_USER'])) {
			// Basic authentication
			$user = $_SERVER['PHP_AUTH_USER'];
			$pass = $_SERVER['PHP_AUTH_PW'];

			$this->basicAuthentication($user, $pass);

		} else {
			// Ask for basic authentication credentials
			header('WWW-Authenticate: Basic realm="MyCars REST Service"');
			header('HTTP/1.0 401 Unauthorized');
		}
	}


	/**
	 * Check if email and password are valid
	 *
	 * @param   $user   string      user email
	 * @param   $pass   string      user password
	 */
	private function basicAuthentication($user, $pass) {
		global $debug;

		$user_model = new User();

		try {
			$this->user_id = $user_model->basicAuthentication($user, $pass);
			$this->authenticated = true;
		} catch (RestException $e) {
			$this->authenticated = false;
			if ($debug) $this->logError($e);
		}
	}


	/**
	 * Check if the API key is valid
	 *
	 * @param   $api_key    string      user API key
	 */
	private function apiKeyAuthentication($api_key) {
		global $debug;

		$user_model = new User();

		try {
			$this->user_id = $user_model->apiKeyAuthentication($api_key);
			$this->authenticated = true;
		} catch (RestException $e) {
			$this->authenticated = false;
			if ($debug) $this->logError($e);
		}
	}


	/**
	 * Check if the user has been authenticated
	 */
	protected function checkAuthentication() {
		if(!$this->authenticated) {
			header('HTTP/1.0 401 Unauthorized');
			die();
		}
	}


	/**
	 * Get model object
	 *
	 * @param   $user_id    string      model_name
	 * @return  BaseModel   model
	 * @throws  \Exception  if the model name has not been set
	 */
	private function getModel($user_id) {
		if (!isset($this->model)) {
			if(!isset($this->model_name))
				throw new \Exception('Model name not set');

			$full_model_name = 'Models\\' . $this->model_name;
			$this->model = new $full_model_name($user_id);
		}

		return $this->model;
	}


	/**
	 * Get data from the database and send JSON
	 *
	 * @param   $method         string      method of the model to be used
	 * @param   $user_id        int         user ID
	 * @param   $content_type   string      type of the response (JSON, image, etc.)
	 * @param   $params         array       params to be passed to the method
	 */
	final protected function doAction($method, $user_id, $content_type, array $params=array()) {
		global $debug, $db_conn;

		try {
			// Check authentication
			if($user_id != -1) $this->checkAuthentication();

			// Get data
			$data = call_user_func_array(array($this->getModel($user_id), $method), $params);

			// Send response
			switch ($content_type) {
				case self::TYPE_JSON:
					$this->sendJson($data);
					break;
			}

		} catch (MissingFieldException $e) {
			// Wrong data provided
			header('HTTP/1.0 400 Bad Request');
			$missing_field = $e->getMissingField();
			if (!empty($missing_field)) $this->sendJson(array('missing_field' => $missing_field));
			if ($debug) $this->logError($e);
			die();

		} catch (InvalidDataException $e) {
			// Wrong data provided
			header('HTTP/1.0 400 Bad Request');
			$invalid_field = $e->getInvalidField();
			if (!empty($invalid_field)) $this->sendJson(array('invalid_field' => $invalid_field));
			if ($debug) $this->logError($e);
			die();

		} catch (UserNotFoundException $e) {
			// No data found
			header('HTTP/1.0 401 Unauthorized');
			$this->sendErrorJson($e);
			if ($debug) $this->logError($e);
			die();

		} catch (InvalidCredentialsException $e) {
			// No data found
			header('HTTP/1.0 401 Unauthorized');
			$this->sendErrorJson($e);
			if ($debug) $this->logError($e);
			die();

		} catch (NotFoundException $e) {
			// No data found
			header('HTTP/1.0 404 Not Found');
			$this->sendErrorJson($e);
			if ($debug) $this->logError($e);
			die();

		} catch (ForbiddenException $e) {
			// User tried to read info of other users
			header('HTTP/1.0 403 Forbidden');
			$this->sendErrorJson($e);
			if ($debug) $this->logError($e);
			die();

		} catch (\Exception $e) {
			// Internal error
			header('HTTP/1.0 500 Internal Server Error');
			$this->logError($e);
			die();

		} finally {
			// Close database connection
			mysqli_close($db_conn);
		}
	}

	/**
	 * Remove read only fields from data array
	 *
	 * @param   $data   array   data array
	 */
	protected function removeReadOnlyFields(&$data) {
		$model = $this->getModel($this->user_id);
		$read_only_fields = $model->getReadOnlyFields();

		if(!empty($read_only_fields))
			foreach($read_only_fields as $field)
				unset($data[$field]);
	}


	/**
	 * Get data passed through a PUT request
	 *
	 * @return  array   body request data
	 */
	protected function getPutData() {
		$data = array();

		if($_SERVER['REQUEST_METHOD'] == 'PUT') {
			// Fetch content and determine boundary
			$raw_data = file_get_contents('php://input');
			$boundary = substr($raw_data, 0, strpos($raw_data, "\r\n"));

			// Fetch each part
			$parts = array_slice(explode($boundary, $raw_data), 1);

			foreach ($parts as $part) {
				// If this is the last part, break
				if ($part == "--\r\n") break;

				// Separate content from headers
				$part = ltrim($part, "\r\n");
				list($raw_headers, $body) = explode("\r\n\r\n", $part, 2);

				// Parse the headers list
				$raw_headers = explode("\r\n", $raw_headers);
				$headers = array();
				foreach ($raw_headers as $header) {
					list($name, $value) = explode(':', $header);
					$headers[strtolower($name)] = ltrim($value, ' ');
				}

				// Parse the Content-Disposition to get the field name, etc.
				if (isset($headers['content-disposition'])) {
					$filename = null;
					preg_match(
						'/^(.+); *name="([^"]+)"(; *filename="([^"]+)")?/',
						$headers['content-disposition'],
						$matches
					);
					list(, , $name) = $matches;
					isset($matches[4]) and $filename = $matches[4];

					// handle your fields here
					switch ($name) {
						// this is a file upload
						case 'userfile':
							file_put_contents($filename, $body);
							break;

						// default for all other files is to populate $data
						default:
							$data[$name] = substr($body, 0, strlen($body) - 2);
							break;
					}
				}

			}
		}

		return $data;
	}


	/**
	 * Send JSON response
	 *
	 * @param   $data   array|string    data to be converted and sent
	 */
	private function sendJson($data) {
		header('Content-Type: application/json; charset=utf8');
		if(!empty($data)) {
			echo json_encode($data);
		}
	}


	/**
	 * Send JSON response containing the exception error code and message
	 *
	 * @param   $e      RestException       exception
	 */
	private function sendErrorJson($e) {
		$code = $e->getErrorCode();
		$message = $e->getMessage();
		$this->sendJson(array('error_code' => $code, 'error_message' => $message));
	}


	/**
	 * Write the error in the PHP error log
	 *
	 * @param   $e      \Exception      error
	 */
	private function logError($e) {

		// Message
		$log = $e->getMessage();
		$log .= "\n\n";

		// Trace
		$log .= $e->getTraceAsString();

		error_log($log);
	}

}