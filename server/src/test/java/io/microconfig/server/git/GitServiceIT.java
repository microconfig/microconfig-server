package io.microconfig.server.git;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GitServiceIT {
    private File local = new File(System.getProperty("user.home") + "/microconfig");

    @Test
    public void should_checkout_open() {
        var remote = "https://github.com/microconfig/configs-layout-example.git";
        var config = new GitConfig();
        config.setRemoteUrl(remote);
        config.setWorkingDir(local);

        //when
        var git = new GitServiceImpl(config);

        //then
        var expected = new File(local, "configs-layout-example");
        assertThat(expected).exists();
        assertThat(expected).isDirectory();
    }

    @Test
    public void should_checkout_private() {
        var remote = "private url";
        var config = new GitConfig();
        config.setRemoteUrl(remote);
        config.setWorkingDir(local);
        config.setUsername("user");
        config.setPassword("passs");

        //when
        var git = new GitServiceImpl(config);

        //then
        var expected = new File(local, "name");
        assertThat(expected).exists();
        assertThat(expected).isDirectory();
    }
}