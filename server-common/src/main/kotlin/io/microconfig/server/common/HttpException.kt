package io.microconfig.server.common

class HttpException(message: String, ex: Exception? = null) : RuntimeException(message) {
    init {
        initCause(ex)
    }
}