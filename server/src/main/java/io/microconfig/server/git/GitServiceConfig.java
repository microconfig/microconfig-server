package io.microconfig.server.git;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
public class GitServiceConfig {
    private String remote;
    @Getter
    private  File local;

    @Bean
    public GitService gitService() {
        Git git = local.exists() ? useLocal() : cloneRemote();
    }


    private Git useLocal() {
        try {
            var repo = new FileRepository(local + "/.git");
            return new Git(repo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Git cloneRemote() {
        try {
            var git = Git.cloneRepository()
                    .setURI(remote)
                    .setDirectory(local)
                    .call();
            log.info("Cloned repository: " + git.getRepository().getDirectory());
            return git;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
