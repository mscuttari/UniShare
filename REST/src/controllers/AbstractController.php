<?php

namespace Controllers;

use Doctrine\ORM\Tools\Setup;
use Doctrine\ORM\EntityManager;
use Exceptions\AuthenticationException;
use Models\User;

abstract class AbstractController {

	private $authenticated;
	private $user;


	/**
	 * Constructor
	 */
	public function __construct() {
		$this->authenticated = false;
		$this->user = null;
	}


	/**
	 * Check authentication
	 *
	 * @throws 	AuthenticationException		if the credentials are invalid
	 */
	protected function checkAuthentication() {
		if ($this->authenticated)
			return;

		if (isset($_SERVER['HTTP_API_KEY'])) {
			// API key authentication (header: api-key)
			$this->user = $this->apiKeyAuthentication($_SERVER['HTTP_API_KEY']);

		} elseif (isset($_SERVER['PHP_AUTH_USER'])) {
			// Basic authentication
			$user = $_SERVER['PHP_AUTH_USER'];
			$pass = $_SERVER['PHP_AUTH_PW'];

			$this->user = $this->basicAuthentication($user, $pass);

		} else {
			// Missing credentials
			throw new AuthenticationException("MISSING_CREDENTIALS");
		}

		$this->authenticated = true;
	}


	/**
	 * Check if email and password are valid
	 *
	 * @param   string		$email			user email
	 * @param   string		$password   	user password
	 *
	 * @return 	User		logged in user instance
	 *
	 * @throws 	AuthenticationException		if the email isn't associated with any user or if the password is wrong
	 */
	private function basicAuthentication($email, $password) : User {
		$entityManager = $this->getEntityManager();
		$usersRepository = $entityManager->getRepository("Models\User");
		$qb = $usersRepository->createQueryBuilder("u");
		$qb->where("u.email = :email")->setParameter("email", $email);

		$user = $qb->getQuery()->getFirstResult();

		if ($user == null)
			throw new AuthenticationException("EMAIL_NOT_FOUND");

		assert($user instanceof User);
		$hashedPassword = UsersController::encryptPassword($password, $user->getSalt());

		if (!hash_equals($user->getPassword(), $hashedPassword)) {
			throw new AuthenticationException("WRONG_PASSWORD");
		}

		return $user;
	}


	/**
	 * Check if the API key is valid
	 *
	 * @param   string		$apiKey		user API key
	 * @return 	User		logged in user instance
	 * @throws 	AuthenticationException	if there is no user with the specified API key
	 */
	private function apiKeyAuthentication(string $apiKey) : User {
		$entityManager = $this->getEntityManager();
		$usersRepository = $entityManager->getRepository("Models\User");
		$qb = $usersRepository->createQueryBuilder("u");
		$qb->where("u.apiKey = :apiKey")->setParameter("apiKey", $apiKey);

		$user = $qb->getQuery()->getFirstResult();

		if ($user == null)
			throw new AuthenticationException("API_KEY_NOT_FOUND");

		assert($user instanceof User);

		return $user;
	}


	/**
	 * Get current user
	 *
	 * @return 	User					logged in user
	 * @throws 	AuthenticationException	if the user is not authenticated
	 */
	protected function getUser() : User {
		if ($this->user != null && $this->user instanceof User)
			return $this->user;

		throw new AuthenticationException("NOT_AUTHENTICATED");
	}


	/**
	 * Get entity manager
	 *
	 * @return	EntityManager		entity manager
	 */
	protected function getEntityManager() : EntityManager {
		global $dbParams, $debug;

		$config = Setup::createAnnotationMetadataConfiguration(array(__DIR__ . "/../models/"), $debug, null, null, false);
		$entityManager = EntityManager::create($dbParams, $config);

		return $entityManager;
	}

}