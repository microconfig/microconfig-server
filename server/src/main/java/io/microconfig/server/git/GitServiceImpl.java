package io.microconfig.server.git;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GitServiceImpl implements GitService {
    private final Git git;
    private final GitConfig config;
    private final File configDir;
    private final Map<String, Instant> pulls = new HashMap<>();

    public GitServiceImpl(GitConfig config) {
        this.config = config;
        this.git = build(config);
        this.configDir = git.getRepository().getDirectory().getParentFile();
    }

    public Git build(GitConfig config) {
        try {
            var localDir = new File(config.getWorkingDir(), config.repoFolderName());
            return localDir.exists() ? useLocal(localDir) : cloneRemote(config, localDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Git useLocal(File localDir) throws IOException {
        return new Git(new FileRepository(new File(localDir, "/.git")));
    }

    private static Git cloneRemote(GitConfig config, File localDir) throws GitAPIException {
        return Git.cloneRepository()
            .setCredentialsProvider(config.credentialsProvider())
            .setURI(config.getRemoteUrl())
            .setDirectory(localDir)
            .setBranch(config.getDefaultBranch())
            .call();
    }

    @Override
    public synchronized File checkoutDefault() {
        return this.checkoutBranch(config.getDefaultBranch());
    }

    @Override
    public synchronized File checkoutBranch(String branch) {
        try {
            checkout(branch);
            pullBranch(branch);
            return configDir;
        } catch (RefNotFoundException e) {
            throw new BranchNotFoundException(branch);
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkout(String branch) throws GitAPIException, IOException {
        if (git.getRepository().findRef(branch) == null) {
            createBranch(branch);
        }
        git.checkout().setName(branch).call();
    }

    private void createBranch(String branch) throws GitAPIException {
        log.debug("Creating and pulling branch {}", branch);
        git.branchCreate()
            .setName(branch)
            .setStartPoint("origin/" + branch)
            .call();
    }

    private void pullBranch(String branch) throws GitAPIException {
        var now = Instant.now();
        if (pulls.computeIfAbsent(branch, __ -> now.minusSeconds(10)).isBefore(now)) {
            log.debug("Pulling branch {}", branch);
            git.pull().setCredentialsProvider(config.credentialsProvider()).call();
            pulls.put(branch, now.plusSeconds(10));
        }
    }

    public File checkoutTag(String tag) {
        try {
            var expected = "refs/tags/"+tag;
            git.fetch().call();
            var foundTag = git.tagList().call().stream()
                .peek(t -> System.out.println(t.getName()))
                .filter(t -> t.getName().equals(expected))
                .findFirst()
                .orElseThrow(() -> new TagNotFoundException(tag));
            git.checkout().setName(foundTag.getName()).call();
            return configDir;
        } catch (GitAPIException e) {
            throw new TagNotFoundException(tag);
        }
    }
}
