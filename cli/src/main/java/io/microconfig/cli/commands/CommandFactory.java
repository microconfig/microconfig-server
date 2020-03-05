package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;

public class CommandFactory {
    public static Command command(String[] args) {
        if (args.length == 0) throw new CliException("No arguments", 1);

        switch (args[0]) {
            case "config":
                return new ConfigCommand(args);
            case "configs":
                return new ConfigsCommand(args);
            default:
                throw new CliException("Unsupported argument " + args[0], 2);
        }
    }
}
