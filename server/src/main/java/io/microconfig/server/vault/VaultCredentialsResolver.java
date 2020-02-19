package io.microconfig.server.vault;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class VaultCredentialsResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return VaultCredentials.class.equals(parameter.getParameterType());
    }

    @Override
    public VaultCredentials resolveArgument(MethodParameter parameter,
                                            ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            WebDataBinderFactory binderFactory) {
        var type = webRequest.getHeader("X-AUTH-TYPE");
        if (type == null) throw new RuntimeException("No credentials type");
        //todo add approle
        if (type.equals("VAULT_TOKEN")) {
            var token = webRequest.getHeader("X-VAULT-TOKEN");
            return new VaultTokenCredentials(token);
        }
        return null;
    }
}
