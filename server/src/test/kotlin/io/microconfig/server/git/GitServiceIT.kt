package io.microconfig.server.git;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class GitServiceIT {
    private File local = new File(System.getProperty("user.home") + "/microconfig");

    @Test
    public void should_checkout_default() {
        var config = gitConfig("https://github.com/microconfig/configs-layout-example.git");

        //when
        var git = new GitServiceImpl(config);
        git.checkoutDefault();

        //then
        testDir("configs-layout-example");
    }

    @Test
    public void should_checkout_open() {
        var config = gitConfig("https://github.com/microconfig/configs-layout-example.git");

        //when
        var git = new GitServiceImpl(config);
        git.checkoutBranch("vault");

        //then
        testDir("configs-layout-example");
    }

    @Test
    public void should_checkout_tag() {
        var config = gitConfig("https://github.com/microconfig/configs-layout-example.git");

        //when
        var git = new GitServiceImpl(config);
        git.checkoutTag("vault-tag");

        //then
        testDir("configs-layout-example");
    }

    @Test
    public void should_checkout_private() {
        var config = gitConfig("private url");
        config.setUsername("user");
        config.setPassword("pass");

        //when
        var git = new GitServiceImpl(config);
        git.checkoutDefault();

        //then
        testDir("name");
    }

    private GitConfig gitConfig(String remote) {
        var config = new GitConfig();
        config.setRemoteUrl(remote);
        config.setWorkingDir(local);
        return config;
    }

    private void testDir(String dirName) {
        var expected = new File(local, dirName);
        assertThat(expected).exists();
        assertThat(expected).isDirectory();
    }
}