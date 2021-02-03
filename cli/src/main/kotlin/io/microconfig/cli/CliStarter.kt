package io.microconfig.cli;

import static io.microconfig.cli.commands.CommandFactory.command;

public class CliStarter {
    public static void main(String[] args) {
        try {
            var code = command(args).execute();
            System.exit(code);
        } catch (CliException e) {
            System.out.println(e.getMessage());
            System.exit(e.getExitCode());
        }
    }
}
