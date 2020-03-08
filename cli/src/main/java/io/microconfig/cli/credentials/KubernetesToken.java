package io.microconfig.cli.credentials;

import java.io.File;
import java.net.http.HttpRequest;

import static io.microconfig.cli.util.FileUtil.readFile;

public class KubernetesToken implements Credentials {
    @Override
    public HttpRequest.Builder addCredentials(HttpRequest.Builder request, String[] args) {
        var token = readFile(new File("/var/run/secrets/kubernetes.io/serviceaccount/token"));
        return request
            .setHeader("X-AUTH-TYPE", "KUBERNETES")
            .setHeader("X-KUBERNETES-TOKEN", token);
    }
}
