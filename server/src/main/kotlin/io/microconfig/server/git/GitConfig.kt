package io.microconfig.server.git

import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.io.File

@ConstructorBinding
@ConfigurationProperties(prefix = "git")
data class GitConfig(
    var workingDir: File,
    var remoteUrl: String,
    var username: String = "open",
    var password: String = "source",
    var defaultBranch: String = "master",
    var pullDelay: Long = 10
) {
    fun credentialsProvider(): CredentialsProvider {
        return UsernamePasswordCredentialsProvider(username, password)
    }

    fun dir(): File {
        return workingDir.resolve("repo")
    }
}