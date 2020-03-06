package io.microconfig.cli.credentials;

import java.net.http.HttpRequest;

public interface Credentials {
    HttpRequest.Builder addCredentials(HttpRequest.Builder request, String[] args);
}
