package io.microconfig.server.git;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GitServiceIT {
    private String remote = "https://github.com/microconfig/configs-layout-example.git";
    private File local = new File(System.getProperty("user.home") + "/microconfig/example");
    private GitServiceImpl git;

    @Before
    public void setUp() {
        git = (GitServiceImpl) GitServiceImpl.init(local, remote);
        git.checkout("master");
    }

    @Test
    public void should_return_refs() {
        var refs = git.refs();
        assertThat(refs.size()).isNotEqualTo(0);
    }

    @Test
    public void should_checkout_branch() {
        git.checkout("master");
    }
}