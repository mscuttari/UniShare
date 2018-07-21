<?php

namespace Models;

use Doctrine\ORM\Mapping\Column;
use Doctrine\ORM\Mapping\Entity;
use Doctrine\ORM\Mapping\GeneratedValue;
use Doctrine\ORM\Mapping\Id;
use Doctrine\ORM\Mapping\Table;

/**
 * @Entity
 * @Table(name="users")
 */
class User {

	/**
	 * @Id
	 * @Column(name="id", type="integer", length=11)
	 * @GeneratedValue
	 * @var	int
	 */
	private $id;

	/**
	 * @Column(name="email", length=255)
	 * @var string
	 */
	private $email;

	/**
	 * @Column(name="password", length=150)
	 * @var string
	 */
	private $password;

	/**
	 * @Column(name="salt", length=16)
	 * @var string
	 */
	private $salt;

	/**
	 * @Column(name="api_key", length=32)
	 * @var string
	 */
	private $apiKey;


	public function getId() : int {
		return $this->id;
	}

	public function setId(int $id) {
		$this->id = $id;
	}

	public function getEmail() : string {
		return $this->email;
	}

	public function setEmail(string $email) {
		$this->email = $email;
	}

	public function getPassword() : string {
		return $this->password;
	}

	public function setPassword(string $password) {
		$this->password = $password;
	}

	public function getSalt() : string {
		return $this->salt;
	}

	public function setSalt(string $salt) {
		$this->salt = $salt;
	}

	public function getApiKey() : string {
		return $this->apiKey;
	}

	public function setApiKey(string $apiKey) {
		$this->apiKey = $apiKey;
	}

}