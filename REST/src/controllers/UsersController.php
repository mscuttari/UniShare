<?php

namespace Controllers;

class UsersController extends AbstractController {

	protected $model_name = 'User';

	public function __construct() {

	}


	/**
	 * Get API key
	 */
	public function login() {
		$this->doAction('login', -1, self::TYPE_JSON, array($_POST));
	}


	/**
	 * Create new user and get API key
	 */
	public function signup() {
		$this->removeReadOnlyFields($_POST);
		$this->doAction('signup', -1, self::TYPE_JSON, array($_POST));
	}


	/**
	 * Reset password
	 */
	public function passwordChange() {
		$this->removeReadOnlyFields($data);
		$this->doAction('passwordChange', -1, self::TYPE_JSON, array($_POST));
	}


	/**
	 * Reset password
	 */
	public function passwordRecover() {
		$this->doAction('passwordRecover', -1, self::TYPE_JSON, array($_POST));
	}


	/**
	 * Delete user
	 */
	public function delete() {
		$this->doAction('deleteUser', -1, self::TYPE_JSON, array($_POST));
	}

}