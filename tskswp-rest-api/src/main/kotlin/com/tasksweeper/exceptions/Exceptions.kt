package com.tasksweeper.exceptions

data class AppError(val error: String)
open class TaskSweeperException(message: String? = null) : Exception(message)

class RegisterException(username: String) : TaskSweeperException("Could not register user $username.")
class InvalidCredentialsException : TaskSweeperException("Login unsuccessful due to invalid credentials.")
class DatabaseNotFoundException(entityName: String? = null) : TaskSweeperException(
    entityName?.let { "The desired element of type $entityName was not found in the database." }
        ?: "The desired element was not found in the database."
)

class InvalidEmailException(email: String) : TaskSweeperException("The email '$email' is not valid.")
class InvalidUsernameException(username: String) : TaskSweeperException("The username '$username' is not valid.")
class InvalidDueDateException(instant: String?) :
    TaskSweeperException("The following date $instant is before the actual date!")

class InvalidDifficultyException(difficulty: String?) :
    TaskSweeperException("Difficulty $difficulty does not exist! Please pick 'Easy', 'Medium' or 'Hard'.")

class InvalidRepetitionException(repetition: String?) :
    TaskSweeperException("Repetition named $repetition does not exist! Please pick 'Daily', 'Weekly', 'Monthly' or 'Yearly'.")

class InvalidTaskIdException(taskId: String) : TaskSweeperException("Task with id '$taskId' does not exist.")
class NotAuthorizedTaskDeletionException(username: String) :
    TaskSweeperException("The account $username is trying to delete a task from another account.")

class NotAuthorizedTaskCompletionException(username: String) :
    TaskSweeperException("The account $username is trying to close a task from another account.")

class TaskAlreadyClosedException(taskId: Long) : TaskSweeperException("Task with id '$taskId' is already closed.")
