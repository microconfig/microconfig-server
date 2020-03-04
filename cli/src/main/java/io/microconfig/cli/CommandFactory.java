package io.microconfig.cli;

public class CommandFactory {
    public static Command command(String[] args) {
        if (args.length == 0) throw new CliException("No arguments", 1);

        switch (args[0]) {
            case "config":
                return new ConfigCommand(args);
            default:
                throw new CliException("Unsupported argument " + args[0], 2);
        }
    }
}
