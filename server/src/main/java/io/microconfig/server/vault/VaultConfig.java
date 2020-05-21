package io.microconfig.server.vault;

import io.microconfig.server.rest.exceptions.BadRequestException;
import io.microconfig.server.vault.credentials.KubernetesTokenCredentials;
import io.microconfig.server.vault.credentials.VaultAppRoleCredentials;
import io.microconfig.server.vault.credentials.VaultCredentials;
import io.microconfig.server.vault.credentials.VaultTokenCredentials;
import lombok.Data;

import java.util.Map;

@Data
public class VaultConfig {
    private final String address;
    private final VaultCredentials credentials;

    public static VaultConfig vaultConfig(Map<String, String> config) {
        var address = getValue(config, "microconfig.vault.address");

        var credentials = switch (getValue(config, "microconfig.vault.auth")) {
            case "kubernetes" -> kubernetes(address, config);
            case "token" -> token(address, config);
            case "approle" -> approle(address, config);
            default -> throw new IllegalStateException("Unsupported auth type");
        };

        return new VaultConfig(address, credentials);
    }

    private static VaultCredentials kubernetes(String address, Map<String, String> config) {
        var path = getValue(config, "microconfig.vault.kubernetes.path");
        var role = getValue(config, "microconfig.vault.kubernetes.role");
        var jwt = getValue(config, "microconfig.vault.kubernetes.jwt");
        return new KubernetesTokenCredentials(address, path, role, jwt);
    }

    private static VaultCredentials token(String address, Map<String, String> config) {
        var token = getValue(config, "microconfig.vault.token");
        return new VaultTokenCredentials(token);
    }

    private static VaultCredentials approle(String address, Map<String, String> config) {
        var path = getValue(config, "microconfig.vault.approle.path");
        var role = getValue(config, "microconfig.vault.approle.role");
        var secret = getValue(config, "microconfig.vault.approle.secret");
        return new VaultAppRoleCredentials(address, path, role, secret);
    }

    private static String getValue(Map<String, String> config, String key) {
        var value = config.get(key);
        if (value == null) throw new BadRequestException("Vault config missing: " + key);
        return value;
    }
}
