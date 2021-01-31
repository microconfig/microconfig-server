package io.microconfig.server.api.exceptions

import io.microconfig.core.properties.repository.ComponentNotFoundException
import io.microconfig.server.common.logger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
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
        val e = ex
        log.error("{}: {} at {}", e.javaClass, e.message, e.stackTrace[0])

    }

    data class ErrorResponse(val error: String?)

}