package io.microconfig.server.rest;

import io.microconfig.server.vault.VaultConfig;
import io.microconfig.server.vault.credentials.KubernetesTokenCredentials;
import io.microconfig.server.vault.credentials.VaultAppRoleCredentials;
import io.microconfig.server.vault.credentials.VaultCredentials;
import io.microconfig.server.vault.credentials.VaultTokenCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Service
@RequiredArgsConstructor
public class VaultCredentialsResolver implements HandlerMethodArgumentResolver {
    private final VaultConfig vaultConfig;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return VaultCredentials.class == parameter.getParameterType();
    }

    @Override
    public VaultCredentials resolveArgument(MethodParameter ignored,
                                            ModelAndViewContainer ignored2,
                                            NativeWebRequest webRequest,
                                            WebDataBinderFactory ignored3) {
        return resolve(webRequest);
    }

    public VaultCredentials resolve(NativeWebRequest webRequest) {
        var type = webRequest.getHeader("X-AUTH-TYPE");
        if (type == null) return null;

        switch (type) {
            case "KUBERNETES":
                return new KubernetesTokenCredentials(vaultConfig);
            case "VAULT_TOKEN":
                return new VaultTokenCredentials();
            case "VAULT_APP_ROLE":
                return new VaultAppRoleCredentials(vaultConfig);
            default:
                throw new IllegalStateException("Unsupported auth type");
        }
    }
}
