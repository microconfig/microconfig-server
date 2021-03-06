package io.microconfig.server.api

import io.microconfig.server.configs.ConfigOptions
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Service
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Service
class ConfigOptionsResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return ConfigOptions::class.java == parameter.parameterType
    }

    override fun resolveArgument(
        _1: MethodParameter,
        _2: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        _3: WebDataBinderFactory?
    ): ConfigOptions {
        val query = query(webRequest)
        val type = query["type"] ?: webRequest.getHeader("X-TYPE")
        val ref = query["ref"] ?: webRequest.getHeader("X-REF")
        val vars = vars(webRequest)
        return ConfigOptions(type, ref, vars)
    }

    private fun vars(webRequest: NativeWebRequest): Map<String, String> {
        val headerValues = webRequest.getHeaderValues("X-VAR") ?: return emptyMap()
        return headerValues
            .map(this::splitVar)
            .toMap()
    }

    private fun splitVar(str: String): Pair<String, String> {
        val idx = str.indexOf('=')
        val key = str.substring(0, idx)
        val value = str.substring(idx + 1)
        return Pair(key, value)
    }

    private fun query(webRequest: NativeWebRequest): Map<String, String> {
        val servletRequest = webRequest as? ServletWebRequest ?: return emptyMap()
        val query = servletRequest.request.queryString ?: return emptyMap()
        return query
            .split('&')
            .map { it.split('=') }
            .associate { it[0] to it[1] }
    }
}

