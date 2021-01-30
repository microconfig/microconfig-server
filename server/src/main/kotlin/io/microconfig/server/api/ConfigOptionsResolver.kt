package io.microconfig.server.api

import io.microconfig.server.configs.ConfigOptions
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Service
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
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
        val type: String? = webRequest.getHeader("X-TYPE")
        val branch: String? = webRequest.getHeader("X-BRANCH")
        val tag: String? = webRequest.getHeader("X-TAG")
        val vars = vars(webRequest)
        return ConfigOptions(type, branch, tag, vars)
    }

    private fun vars(webRequest: NativeWebRequest): Map<String, String> {
        val headerValues = webRequest.getHeaderValues("X-VAR") ?: return emptyMap()
        return headerValues.asSequence()
            .map(this::splitVar)
            .toMap()
    }

    private fun splitVar(str: String): Pair<String, String> {
        val idx = str.indexOf('=')
        val key = str.substring(0, idx)
        val value = str.substring(idx + 1)
        return Pair(key, value)
    }
}

