package io.microconfig.server.vault;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "vault")
public class VaultConfig {
    private String address;
    private List<AppRoleConfig> appRoles;

    private final String path = "team-approle";
    private final String roleId = "526cae12-6958-b99e-bfaa-3dde5d0ee5b1";

    private final String kubePath = "team-cluster";
    private final String kubeRole = "team-test";

    @Data
    static class AppRoleConfig {
        private String path;
        private String role;
        private List<AppRoleMatch> matches;
    }

    @Data
    static class AppRoleMatch {
        private String env;
        private List<String> components;
    }
}
