package io.microconfig.server.util;

import io.microconfig.server.rest.exceptions.ServerErrorException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.stream.Collectors.joining;

@Slf4j
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
            return httpClient.send(request, ofString());
        } catch (HttpConnectTimeoutException e) {
            throw new ServerErrorException("HTTP timeout: " + request.uri().toString());
        } catch (IOException e) {
            log.error("Failed http call: {}", e.getClass());
            throw new RuntimeException("IO Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        }
    }
}
