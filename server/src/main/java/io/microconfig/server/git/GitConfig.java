package io.microconfig.server.git;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Data
@Component
@Slf4j
@ConfigurationProperties(prefix = "git")
public class GitConfig {
    private File workingDir;
    private String remoteUrl;
    private String username;
    private String password;
    private String defaultBranch;

    public CredentialsProvider credentialsProvider() {
        return new UsernamePasswordCredentialsProvider(username, password);
    }

    public String repoFolderName() {
        var slash = remoteUrl.lastIndexOf('/');
        var dot = remoteUrl.lastIndexOf('.');
        return remoteUrl.substring(slash, dot);
    }
}