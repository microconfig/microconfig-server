package io.microconfig.cli.commands;

import io.microconfig.cli.CliFlags;
import io.microconfig.cli.credentials.CredentialsProvider;

import java.net.http.HttpRequest;

public abstract class Command {
    static final CredentialsProvider credentials = new CredentialsProvider();
    final CliFlags flags;
    final String[] args;

    public Command(String[] args) {
        this.args = args;
        this.flags = new CliFlags(args);
    }

    void addFlags(HttpRequest.Builder request) {
        flags.kubernetesToken().ifPresent(token -> credentials.addKubernetesToken(request, token));
        flags.vaultToken().ifPresent(token -> credentials.addVaultToken(request, token));
        flags.branch().ifPresent(b -> request.setHeader("X-BRANCH", b));
        flags.tag().ifPresent(t -> request.setHeader("X-TAG", t));
    }

    public abstract int execute();
}
