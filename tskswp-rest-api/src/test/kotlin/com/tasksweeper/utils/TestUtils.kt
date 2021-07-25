package com.tasksweeper.utils

import com.tasksweeper.authentication.JWT
import io.ktor.server.testing.*


fun TestApplicationRequest.addJwtHeader(jwt: JWT, username: String) = addHeader(
    "Authorization", "Bearer ${
        jwt.sign(username)
    }"
)

fun TestApplicationRequest.addJwtHeader(jwt: String) = addHeader(
    "Authorization", "Bearer $jwt"
)

fun TestApplicationRequest.addContentTypeHeader() = addHeader("content-type", "application/json")
