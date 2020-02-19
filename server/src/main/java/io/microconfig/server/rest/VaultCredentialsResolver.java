package io.microconfig.server.rest;

import io.microconfig.server.vault.VaultCredentials;
import io.microconfig.server.vault.VaultTokenCredentials;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static java.util.Objects.requireNonNull;

@Component
public class VaultCredentialsResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return VaultCredentials.class.equals(parameter.getParameterType());
    }

    @Override
    public VaultCredentials resolveArgument(MethodParameter ignored,
                                            ModelAndViewContainer ignored2,
                                            NativeWebRequest webRequest,
                                            WebDataBinderFactory ignored3) {
        var type = requireNonNull(webRequest.getHeader("X-AUTH-TYPE"), "No credentials type");

        //todo add approle
        if (type.equals("VAULT_TOKEN")) {
            var token = webRequest.getHeader("X-VAULT-TOKEN");
            return new VaultTokenCredentials(token);
        }

        throw new IllegalStateException("Can't resolve vault credentials");
    }
}
