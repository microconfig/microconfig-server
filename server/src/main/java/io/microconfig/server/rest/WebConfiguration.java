package io.microconfig.server.rest;

import io.microconfig.server.vault.VaultAppRoleCredentials;
import io.microconfig.server.vault.VaultCredentials;
import io.microconfig.server.vault.VaultTokenCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod(GET);
        config.addAllowedMethod(POST);
        config.addAllowedMethod(OPTIONS);
        config.addAllowedMethod(PATCH);
        config.addAllowedMethod(DELETE);
        source.registerCorsConfiguration("/api/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new VaultCredentialsResolver());
    }

    private static class VaultCredentialsResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return VaultCredentials.class == parameter.getParameterType();
        }

        @Override
        public VaultCredentials resolveArgument(MethodParameter ignored,
                                                ModelAndViewContainer ignored2,
                                                NativeWebRequest webRequest,
                                                WebDataBinderFactory ignored3) {
            var type = webRequest.getHeader("X-AUTH-TYPE");
            if (type == null) return null;

            switch (type) {
                case "VAULT_TOKEN":
                    return new VaultTokenCredentials(webRequest.getHeader("X-VAULT-TOKEN"));
                case "VAULT_APP_ROLE":
                    return new VaultAppRoleCredentials(webRequest.getHeader("X-VAULT-SECRET-ID"));
                default:
                    throw new IllegalStateException("Can't resolve vault credentials");
            }
        }
    }
}