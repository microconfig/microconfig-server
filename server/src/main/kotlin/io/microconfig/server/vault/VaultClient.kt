package io.microconfig.server.vault;

public interface VaultClient {
    String fetchKV(String property);
}
