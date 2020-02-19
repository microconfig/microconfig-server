package io.microconfig.server.git;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
class GitServiceConfiguration {
    @Bean
    public GitService gitService(@Value("${git.localDir}") File localDir, @Value("${git.remoteUrl}") String remoteUrl) {
        GitService service = GitServiceImpl.init(localDir, remoteUrl);
        service.checkout("master");
        return service;
    }
}