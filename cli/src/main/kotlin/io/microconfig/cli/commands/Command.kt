package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;
import io.microconfig.cli.ssl.TrustManagerFactory;

import javax.net.ssl.SSLContext;
import java.net.http.HttpRequest;

import static io.microconfig.cli.ssl.TrustManagerFactory.defaultTrust;
import static io.microconfig.cli.ssl.TrustManagerFactory.trustAll;

public abstract class Command {
    final CliFlags flags;
    final String[] args;

    public Command(String[] args) {
        this.args = args;
        this.flags = new CliFlags(args);
    }

    void addHeaders(HttpRequest.Builder request) {
        flags.type().ifPresent(t -> request.setHeader("X-TYPE", t));
        flags.branch().ifPresent(b -> request.setHeader("X-BRANCH", b));
        flags.tag().ifPresent(t -> request.setHeader("X-TAG", t));
        flags.vars().forEach((key, value) -> request.header("X-VAR", key + "=" + value));
    }

    String server() {
        return flags.server()
                .orElse(System.getenv().getOrDefault("MCS_ADDRESS", "http://localhost:8080"));
    }

    String component(String message) {
        if (args.length < 2) throw new CliException(message, 3);
        return args[1];
    }

    public abstract int execute();

    SSLContext sslContext() {
        if (flags.skipTls()) return trustAll();
        return flags.rootCa().map(TrustManagerFactory::rootCa).orElse(defaultTrust());
    }

}
