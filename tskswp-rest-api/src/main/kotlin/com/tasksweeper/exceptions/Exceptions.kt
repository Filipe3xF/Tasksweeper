package com.tasksweeper.exceptions

open class TaskSweeperException(message: String? = null) : Exception(message)

class AuthenticationException : TaskSweeperException()
class AuthorizationException : TaskSweeperException()
class RegisterException(username: String) : TaskSweeperException("Could not register user $username.")
class InvalidCredentialsException : TaskSweeperException("Login unsuccessful due to invalid credentials.")