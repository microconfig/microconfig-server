package io.microconfig.server.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

inline fun <reified T : Any> T.logger(): Logger = LoggerFactory.getLogger(unwrap(T::class.java))

fun <T : Any> unwrap(javaClass: Class<T>) = javaClass.enclosingClass?.takeIf {
    it.kotlin.companionObject?.java == javaClass
} ?: javaClass