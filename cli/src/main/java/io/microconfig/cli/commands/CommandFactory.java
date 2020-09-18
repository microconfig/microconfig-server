package io.microconfig.cli.commands;

import io.microconfig.cli.CliException;

public class CommandFactory {
    public static Command command(String[] args) {
        if (args.length == 0) throw new CliException("Usage: microctl [command] [component] [flags]\nNo command provided.\nSupported commands are [show, save, version, help]", 1);

        switch (args[0]) {
            case "show":
                return new ShowCommand(args);
            case "save":
                return new SaveCommand(args);
            case "version":
                return new VersionCommand(args);
            case "help":
                return new HelpCommand(args);
            default:
                throw new CliException("Unsupported argument " + args[0], 2);
        }
    }
}
