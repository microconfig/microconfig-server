package io.microconfig.cli.util;

import io.microconfig.cli.CliException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;

public class HttpUtil {

    public static HttpRequest.Builder httpGET(URI uri, Optional<Integer> timeout) {
        return newBuilder(uri).GET()
            .timeout(ofSeconds(timeout.orElse(2)));
    }

    public static String httpSend(HttpRequest request) {
        try {
            var response = newHttpClient().send(request, ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                var error = processError(response);
                throw new CliException("Server Error: " + error, 200);
            }
        } catch (IOException e) {
            throw new CliException("Failed during network call: " + e.getMessage(), 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CliException("Interrupted during network call", 666);
        }
    }

    public static String processError(HttpResponse<String> response) {
        var json = JsonUtil.parse(response.body());
        return json.get("error").asText();
    }
}
