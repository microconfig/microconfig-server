package io.microconfig.server.git;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class GitServiceIT {
    private String remote = "https://github.com/microconfig/configs-layout-example.git";
    private File local = new File(System.getProperty("user.home") + "/microconfig/config");
    private GitService git;

    @Before
    public void setUp() {
        git = GitServiceImpl.init(local, remote);
        git.checkout("master");
    }

    @Test
    public void should_checkout_branch() {
        git.checkout("master");
    }
}