package com.tasksweeper.controller

import io.ktor.http.content.*
import io.ktor.routing.*

fun Routing.apiController() {
    static("api") {
        resource("documentation", "openapi.json")
    }
}