package io.microconfig.server.vault;

import lombok.Data;

@Data
public class VaultConfig {
    private final String address;
    private final String credentialsType;
}
