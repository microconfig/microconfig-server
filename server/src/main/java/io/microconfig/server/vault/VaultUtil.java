package io.microconfig.server.vault;

import com.fasterxml.jackson.databind.JsonNode;
import io.microconfig.server.vault.exceptions.VaultAuthException;

import java.net.http.HttpResponse;

import static io.microconfig.server.util.JsonUtil.parseJson;

public class VaultUtil {

    public static JsonNode validateResponse(HttpResponse<String> response) {
        var node = parseJson(response.body());
        if (response.statusCode() != 200) {
            if (node.get("errors") != null) {
                throw new VaultAuthException("Vault Auth failed with errors: " + node.get("errors").toString());
            } else {
                throw new VaultAuthException("Vault Auth failed with http status: " + response.statusCode());
            }
        }
        return node;
    }
}
