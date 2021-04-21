package com.tasksweeper.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.time.Duration
import java.util.*

class JWT(secret: String = "shhhhh, it's a secret.") {
    private val expirationTime = Duration.ofHours(1).toMillis()
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun sign(username: String): String = JWT.create()
        .withClaim("username", username)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + expirationTime)
}