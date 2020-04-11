package io.microconfig.server.vault.credentials;

import io.microconfig.server.configs.DynamicVarsResolverStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultTokenCredentials implements VaultCredentials {
    private String token;

    @Override
    public String getToken(DynamicVarsResolverStrategy dynamicVars) {
        if (token == null) {
            //todo should resolve through microconfig api instead of direct access
            token = dynamicVars.getValue("microconfig.vault.token");
        }
        return token;
    }
}