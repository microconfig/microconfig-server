package io.microconfig.cli.util;

import io.microconfig.cli.CliException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static java.util.stream.Collectors.joining;

public class HttpUtil {
    public static String getQuery(Map<String, String> query) {
        var q = query.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(joining("&"));

        return q.isEmpty()
            ? q
            : "?" + q;
    }

    public static HttpRequest.Builder httpGET(URI uri) {
        return HttpRequest.newBuilder(uri)
            .GET()
            .timeout(Duration.ofSeconds(4));
    }

    public static String httpSend(HttpRequest request) {
        try {
            var response = newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new CliException("Bad response code: " + response.statusCode(), 200);
            }
        } catch (IOException e) {
            throw new CliException("Failed during network call: " + e.getMessage(), 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CliException("Interrupted during network call", 666);
        }
    }
}
