package io.microconfig.server.api

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(val configOptionsResolver: ConfigOptionsResolver) : WebMvcConfigurer {

    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config: CorsConfiguration = CorsConfiguration().applyPermitDefaultValues()
        config.addAllowedMethod(HttpMethod.GET)
//        config.addAllowedMethod(POST);
//        config.addAllowedMethod(OPTIONS);
//        config.addAllowedMethod(PATCH);
//        config.addAllowedMethod(DELETE);
        source.registerCorsConfiguration("/api/**", config)
        return FilterRegistrationBean(CorsFilter(source))
            .apply { order = 0 }
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(configOptionsResolver)
    }
}