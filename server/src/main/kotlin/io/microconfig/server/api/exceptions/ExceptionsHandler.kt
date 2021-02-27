package io.microconfig.server.api.exceptions

import io.microconfig.core.environments.repository.EnvironmentException
import io.microconfig.core.properties.ResolveException
import io.microconfig.core.properties.repository.ComponentNotFoundException
import io.microconfig.server.common.logger
import io.microconfig.server.git.exceptions.RefNotFound
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
@RestController
class ExceptionsHandler {
    val log = logger()

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ResponseEntity<ErrorResponse> {
        logEx(ex)
        return when (ex) {
            is ResolveException -> response(ex.message, BAD_REQUEST)
            is RefNotFound -> response(ex.message, BAD_REQUEST)
            is EnvironmentException -> response(ex.message, BAD_REQUEST)
            is ServerErrorException -> response(ex.message, INTERNAL_SERVER_ERROR)
            is NotFoundException -> response(ex.message, NOT_FOUND)
            is ComponentNotFoundException -> response(ex.message, NOT_FOUND)
            is ForbiddenException -> response(ex.message, FORBIDDEN)
            is BadRequestException -> response(ex.message, BAD_REQUEST)
            else -> response("Something went wrong", BAD_REQUEST)
        }
    }

    private fun response(error: String?, status: HttpStatus): ResponseEntity<ErrorResponse> {
        return ResponseEntity<ErrorResponse>(ErrorResponse(error), status)
    }

    private fun logEx(ex: Exception) {
        val c = ex.javaClass.name
        when {
            c.startsWith("io.microconfig") -> log.error(ex.message)
            else -> log.error("$c: ${ex.message} at ${ex.stackTrace[0]}")
        }
    }

    data class ErrorResponse(val error: String?)

}