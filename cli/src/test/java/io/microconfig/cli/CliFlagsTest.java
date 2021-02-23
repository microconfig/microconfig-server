package io.microconfig.cli;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CliFlagsTest {
    String[] args = new String[]{
        "config", "payment-db-patcher",
        "-e", "test", "--branch", "vault",
        "--set", "microconfig.vault.address=http://localhost:8200", "--set", "microconfig.vault.auth=kubernetes"
    };

    @Test
    public void multipleSetParsing() {
        var flags = new CliFlags(args);
        var vars = flags.vars();
        assertEquals(2, vars.size());
    }
}