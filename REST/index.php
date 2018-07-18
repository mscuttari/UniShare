<?php

require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/settings.php';
require_once __DIR__ . '/db_conn.php';

// Maintenance status
if ($maintenance) {
	header('HTTP/1.0 503 Service Unavailable');
	die();
}

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
switch ($routeInfo[0]) {
	case FastRoute\Dispatcher::NOT_FOUND:
		header('HTTP/1.0 404 Not Found');
		die();
		break;

	case FastRoute\Dispatcher::METHOD_NOT_ALLOWED:
		$allowedMethods = $routeInfo[1];
		header('HTTP/1.0 405 Method Not Allowed');
		die();
		break;

	case FastRoute\Dispatcher::FOUND:
		$handler = $routeInfo[1];
		$vars = $routeInfo[2];
		list($class, $method) = explode("/", $handler, 2);
		$class = "Controllers\\" . $class;
		call_user_func_array(array(new $class, $method), $vars);
		break;
}