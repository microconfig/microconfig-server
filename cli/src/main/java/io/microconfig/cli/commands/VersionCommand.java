package io.microconfig.cli.commands;

public class VersionCommand extends Command {

    public VersionCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        System.out.println("Version 0.1.2");
        return 0;
    }
}
