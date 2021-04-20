package com.tasksweeper.exceptions

open class TaskSweeperException(message: String? = null) : Exception(message)

class AuthenticationException : TaskSweeperException()
class AuthorizationException : TaskSweeperException()
