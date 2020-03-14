package io.microconfig.server.rest;

import io.microconfig.server.configs.ConfigOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Service
@RequiredArgsConstructor
public class ConfigOptionsResolver implements HandlerMethodArgumentResolver {

    private final VaultCredentialsResolver vaultCredentialsResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ConfigOptions.class == parameter.getParameterType();
    }

    @Override
    public ConfigOptions resolveArgument(MethodParameter ignored,
                                         ModelAndViewContainer ignored2,
                                         NativeWebRequest webRequest,
                                         WebDataBinderFactory ignored3) {
        var vaultCredentials = vaultCredentialsResolver.resolve(webRequest);
        var branch = webRequest.getHeader("X-BRANCH");
        var tag = webRequest.getHeader("X-TAG");
        return new ConfigOptions(branch, tag, vaultCredentials);
    }
}
