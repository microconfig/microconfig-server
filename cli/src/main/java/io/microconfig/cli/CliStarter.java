package io.microconfig.cli;

import io.microconfig.cli.commands.CommandFactory;

public class CliStarter {
    public static void main(String[] args) {
        try {
            var command = CommandFactory.command(args);
            var code = command.execute();
            System.exit(code);
        } catch (CliException e) {
            System.out.println(e.getMessage());
            System.exit(e.getExitCode());
        }
    }
}
