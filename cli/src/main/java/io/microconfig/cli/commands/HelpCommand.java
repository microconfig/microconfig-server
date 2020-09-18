package io.microconfig.cli.commands;

public class HelpCommand extends Command {

    public HelpCommand(String[] args) {
        super(args);
    }

    @Override
    public int execute() {
        System.out.println("Usage: microctl [command] [component] [flags]");
        System.out.println("Common Flags: ");
        System.out.println("  --branch:  git branch to use");
        System.out.println("  --tag:     git tag to use");
        System.out.println("  --timeout: server calls timeout in seconds, by default 10");
        System.out.println("  --server:  server url (or MCS_ADDRESS env variable), by default [http://localhost:8080]");
        return 0;
    }
}
