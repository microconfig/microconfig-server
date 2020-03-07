package io.microconfig.server.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.microconfig.server.vault.credentials.VaultCredentials;
import io.microconfig.server.vault.exceptions.VaultException;
import io.microconfig.server.vault.exceptions.VaultSecretNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.http.HttpClient.newHttpClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class VaultClientImpl implements VaultClient {
    private static final HttpClient httpClient = newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private final VaultConfig config;

    @Override
    public String fetchSecret(VaultCredentials credentials, String property) {
        int dotIndex = property.lastIndexOf('.');
        String path = property.substring(0, dotIndex);
        String key = property.substring(dotIndex + 1);
        log.debug("Fetching {} {}", path, key);

        try {
            var node = readPath(path, credentials.getToken(config));
            var value = node.get(key);
            if (value == null) throw new VaultSecretNotFound(property);
            return value.asText();
        } catch (Exception e) {
            throw new VaultException();
        }
    }

    private JsonNode readPath(String path, String token) throws Exception {
        var splitPath = splitPath(path);
        var request = HttpRequest.newBuilder(URI.create(config.getAddress() + "/v1/" + splitPath[0] + "/data" + splitPath[1]))
            .setHeader("X-Vault-Token", token)
            .timeout(Duration.ofSeconds(2))
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var json = mapper.readTree(response.body());
        return json.path("data").path("data");
    }

    private String[] splitPath(String path) {
        int slash = path.indexOf('/');
        return new String[]{path.substring(0, slash), path.substring(slash)};
    }
}
