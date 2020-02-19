package io.microconfig.server.vault;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class VaultClientImpl implements VaultClient {
    private final VaultConfig config;

    @Override
    public String fetchSecret(VaultCredentials credentials, String property) {
        int dotIndex = property.lastIndexOf('.');
        String path = property.substring(0, dotIndex);
        String key = property.substring(dotIndex + 1);
        log.debug("Fetching {} {}", path, key);

        try {
            return credentials.toVault(config)
                    .logical()
                    .read(path)
                    .getData()
                    .get(key);
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }
    }
}
