package io.microconfig.server.vault;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "vault")
public class VaultConfig {
    private String address;
}
