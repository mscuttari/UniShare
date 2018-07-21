<?php

use Exceptions\MethodNotAllowedException;
use Exceptions\NotFoundException;
use Exceptions\RestException;
use Exceptions\ServiceUnavailableException;

require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/settings.php';
require_once __DIR__ . '/db_conn.php';

$dispatcher = FastRoute\cachedDispatcher(function(FastRoute\RouteCollector $r) {
	global $url_prefix;

	// Users
	$r->addRoute('POST', $url_prefix . '/users/login[/]', 'UsersController/login');
	$r->addRoute('POST', $url_prefix . '/users/signup[/]', 'UsersController/signup');
},
	[
		'cacheFile' => $route_cache,
		'cacheDisabled' => $debug
	]
);

// Fetch method and URI from somewhere
$httpMethod = $_SERVER['REQUEST_METHOD'];
$uri = $_SERVER['REQUEST_URI'];

// Strip query string (?foo=bar) and decode URI
if (false !== $pos = strpos($uri, '?')) {
	$uri = substr($uri, 0, $pos);
}
$uri = rawurldecode($uri);

$routeInfo = $dispatcher->dispatch($httpMethod, $uri);

try {
	// Maintenance status
	if ($maintenance) {
		throw new ServiceUnavailableException();
	}

	switch ($routeInfo[0]) {
		case FastRoute\Dispatcher::NOT_FOUND:
			throw new NotFoundException();

		case FastRoute\Dispatcher::METHOD_NOT_ALLOWED:
			throw new MethodNotAllowedException();

		case FastRoute\Dispatcher::FOUND:
			$handler = $routeInfo[1];
			$vars = $routeInfo[2];
			list($class, $method) = explode("/", $handler, 2);
			$class = "Controllers\\" . $class;
			$data = call_user_func_array(array(new $class, $method), $vars);
			sendJson($data);
	}

} catch (RestException $e) {
	header($e->getHttpResponseCode());
	sendJson($e->getErrorData());
	if ($debug) logError($e);
	die();
}


/**
 * Send JSON response
 *
 * @param   $data   array|string    data to be converted and sent
 */
function sendJson(array $data) {
	header('Content-Type: application/json; charset=utf8');

	if (!empty($data)) {
		echo json_encode($data);
	}
}


/**
 * Write the error in the PHP error log
 *
 * @param   RestException	$e		exception
 */
function logError(RestException $e) {
	$log = "";

	// Message
	$errorData = $e->getErrorData();

	if (!empty($errorData)) {
		$log .= print_r($e->getErrorData(), true);
		$log .= "\n\n";
	}

	// Trace
	$log .= $e->getTraceAsString();

	error_log($log);
}