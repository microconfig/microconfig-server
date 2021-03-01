package io.microconfig.server.client

class MicroconfigException(error: String, val code: Int) : RuntimeException(error)
