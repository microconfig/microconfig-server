package io.microconfig.server.vault.credentials;

import io.microconfig.server.configs.DynamicVarsResolverStrategy;

public interface VaultCredentials {
    //todo temporary until microconfig ddd is finished
    String getToken(DynamicVarsResolverStrategy dynamicVarsResolverStrategy);
}
