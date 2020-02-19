package io.microconfig.server.vault;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaultClientImpl implements VaultClient {
    private final VaultConfig config;

    @Override
    public String fetchSecret(VaultCredentials credentials, String placeholder) {
        int dotIndex = placeholder.lastIndexOf('.');
        String path = placeholder.substring(0, dotIndex);
        String key = placeholder.substring(dotIndex + 1);
        log.debug("Fetching {} {}", path, key);

        try {
            return credentials.insert(config)
                    .logical()
                    .read(path)
                    .getData()
                    .get(key);
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlaceholderResolver asResolver(VaultCredentials credentials) {
        return new VaultResolver(this, credentials);
    }
}
