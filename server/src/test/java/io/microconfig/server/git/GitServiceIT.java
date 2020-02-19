package io.microconfig.server.git;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GitServiceIT {
    private String remote = "https://github.com/microconfig/configs-layout-example.git";
    private File local = new File(System.getProperty("user.home") + "/microconfig/example");
    private GitService git;

    @Before
    public void setUp() {
        git = new GitService(remote, local);
        git.checkOutCurrentBranch();
    }

    @Test
    public void should_return_refs() {
        var refs = git.refs();
        assertThat(refs.size()).isEqualTo(1);
    }

    @Test
    public void should_checkout_branch() {
        git.checkoutBranch("master");
    }
}