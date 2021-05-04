package com.tasksweeper.exceptions

data class AppError(val error: String)
open class TaskSweeperException(message: String? = null) : Exception(message)

class RegisterException(username: String) : TaskSweeperException("Could not register user $username.")
class InvalidCredentialsException : TaskSweeperException("Login unsuccessful due to invalid credentials.")
class DatabaseNotFoundException : TaskSweeperException("The desired element was not found in the database.")
class InvalidEmailException(email: String) : TaskSweeperException("The email '$email' is not valid.")
class InvalidUsernameException(username: String) : TaskSweeperException("The username '$username' is not valid.")
