<?php

namespace Controllers;

use Exceptions\InvalidDataException;
use Exceptions\MissingFieldException;
use Models\User;

class UsersController extends AbstractController {

	const PASSWORD_MIN_LENGTH = 8;				// Password minimum length
	const PASSWORD_SALT_LENGTH = 16;			// Password salt length
	const PASSWORD_ENCRYPT_ALGORITH = "$6$";	// SHA-512


	/**
	 * Login
	 *
	 * @return	array		array containing the user API key (field "api_key")
	 */
	public function login() :array {
		$this->checkAuthentication();
		return array("api_key" => $this->getUser()->getApiKey());
	}


	/**
	 * Signup
	 *
	 * @return	array		array containing the user API key (field "api_key")
	 *
	 * @throws 	InvalidDataException	if the email is invalid
	 * @throws 	MissingFieldException	if email of password are missing
	 */
	public function signup() : array {
		// Email
		if (empty($_POST["email"])) throw new MissingFieldException("EMAIL");
		$email = $_POST["email"];
		if (!self::isEmailValid($email)) throw new InvalidDataException("EMAIL");

		// Clear password
		if (empty($_POST["password"])) throw new MissingFieldException("PASSWORD");
		$password = $_POST["password"];
		if (!self::isPasswordValid($password)) throw new InvalidDataException("PASSWORD");

		// Check if the email is already in use
		$entityManager = $this->getEntityManager();
		$usersRepository = $entityManager->getRepository("Models\User");
		$users = $usersRepository->createQueryBuilder("u")
			->where("u.email = :email")
			->setParameter("email", $email)
			->getQuery()
			->getResult();

		if (!empty($users))
			throw new InvalidDataException("EMAIL");

		// Create user
		$user = new User();

		$user->setEmail($email);
		$user->setSalt(self::createSalt());
		$user->setPassword(self::encryptPassword($password, $user->getSalt()));
		$user->setApiKey(self::createApiKey());

		$entityManager->persist($user);
		$entityManager->flush();

		// Send API key
		return array("api_key" => $user->getApiKey());
	}


	/**
	 * Check if the email is valid
	 *
	 * @param   string		$email		email to be checked
	 * @return  bool  		true if the email is valid
	 */
	private static function isEmailValid(string $email) : bool {
		return filter_var($email, FILTER_VALIDATE_EMAIL);
	}


	/**
	 * Check if the password is valid
	 *
	 * @param   string		$password	password to be checked
	 * @return  bool  		true if the password is valid
	 */
	private static function isPasswordValid(string $password) : bool {
		return strlen($password) >= self::PASSWORD_MIN_LENGTH;
	}


	/**
	 * Encrypt password
	 *
	 * @param   string		$password		password to be encrypted
	 * @param	string		$salt			salt to be used to encrypt the password
	 *
	 * @return 	string		encrypted password
	 */
	public static function encryptPassword(string $password, string $salt) : string {
		return crypt($password, self::PASSWORD_ENCRYPT_ALGORITH . $salt);
	}


	/**
	 * Create salt to be used for password encryption
	 *
	 * @return	string		salt
	 */
	private static function createSalt() : string {
		return substr(md5(openssl_random_pseudo_bytes(self::PASSWORD_SALT_LENGTH)), 0, self::PASSWORD_SALT_LENGTH);
	}


	/**
	 * Create API key
	 *
	 * @return  string		API key
	 */
	private static function createApiKey() : string {
		return md5(microtime().rand());
	}

}