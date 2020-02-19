package io.microconfig.server.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;

public interface VaultCredentials extends PluginContext {
    Vault insert(VaultConfig config);
}
