package io.microconfig.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {
    private final String remote;
    @Getter
    private final File local;
    private Git git;
    private String currentBranch = "master";

    @PostConstruct
    public void postConstruct() {
        git = local.exists() ? useLocal() : cloneRemote();
        checkoutBranch(currentBranch);
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

    public void checkoutBranch(String branch) {
        try {
            git.checkout().setName(branch).call();
            currentBranch = branch;
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ref> refs() {
        try {
            return git.branchList().call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
