package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;
import io.microconfig.cli.CliFlags;

import java.net.http.HttpRequest;

public abstract class Command {
    final CliFlags flags;
    final String[] args;

    public Command(String[] args) {
        this.args = args;
        this.flags = new CliFlags(args);
    }

    void addFlags(HttpRequest.Builder request) {
        flags.branch().ifPresent(b -> request.setHeader("X-BRANCH", b));
        flags.tag().ifPresent(t -> request.setHeader("X-TAG", t));
        flags.vars().forEach((key, value) -> request.setHeader("X-VAR", key + "=" + value));
    }

    String server() {
        var server = System.getenv("MCS_ADDRESS");
        return server != null ? server : "http://localhost:8080";
    }

    String component() {
        if (args.length < 2) throw new CliException("Component not provided", 3);
        return args[1];
    }

    public abstract int execute();
}
