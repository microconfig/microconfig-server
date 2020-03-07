package io.microconfig.server.util;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static java.util.stream.Collectors.joining;

public class HttpUtil {

    private static final HttpClient httpClient = newHttpClient();

    public static String getQuery(Map<String, String> query) {
        var q = query.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(joining("&"));

        return q.isEmpty()
            ? q
            : "?" + q;
    }

    public static HttpResponse<String> httpSend(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException("IO Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }
}
