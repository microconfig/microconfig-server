package io.microconfig.server.git

import io.microconfig.server.git.exceptions.RefNotFound
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class GitServiceIT {
    private val local = File(System.getProperty("user.home") + "/microconfig")
    private val repo = "https://github.com/microconfig/microconfig-quickstart.git"

    @Test
    fun should_checkout_default() {
        val config = gitConfig(repo)

        //when
        val git = GitServiceImpl(config)
        git.checkoutRef(null)

        //then
        testDir(config, "master")
    }

    @Test
    fun should_checkout_branch() {
        val config = gitConfig(repo)

        //when
        val git = GitServiceImpl(config)
        git.checkoutRef("vault")

        //then
        testDir(config, "vault")
    }

    @Test
    fun should_checkout_tag() {
        val config = gitConfig("https://github.com/microconfig/microconfig-quickstart.git")

        //when
        val git = GitServiceImpl(config)
        git.checkoutRef("test-tag")

        //then
        testDir(config, "test-tag")
    }

    @Test
    fun should_fail_on_missing_tag() {
        val config = gitConfig("https://github.com/microconfig/microconfig-quickstart.git")
        val git = GitServiceImpl(config)

        //expected
        assertThrows<RefNotFound> { git.checkoutRef("missing-tag") }
        val expected = File(config.dir(), "missing-tag")
        assertThat(expected).doesNotExist()
    }

    //    @Test
    fun should_checkout_private() {
        val config = gitConfig("private url")
        config.username = "user"
        config.password = "pass"

        //when
        val git = GitServiceImpl(config)
        git.checkoutRef("master")

        //then
        testDir(config, "name")
    }

    private fun gitConfig(remote: String): GitConfig {
        return GitConfig(local, remote)
    }

    private fun testDir(config: GitConfig, dirName: String) {
        val expected = File(config.dir(), dirName)
        assertThat(expected).exists()
        assertThat(expected).isDirectory
    }
}