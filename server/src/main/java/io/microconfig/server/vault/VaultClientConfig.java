package io.microconfig.server.vault;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaultClientConfig {
    @Bean
    public VaultClient vaultClient(@Value("${vault.address}") String address) throws VaultException {
        return new VaultClientImpl(new VaultConfig()
                .address(address)
                .engineVersion(2)
                .build());
    }
}
