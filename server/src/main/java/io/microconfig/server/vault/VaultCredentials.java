package io.microconfig.server.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import io.microconfig.server.vault.PluginContext;

public interface VaultCredentials extends PluginContext {
    Vault insert(VaultConfig config);
}
