package io.microconfig.server.git;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GitServiceImpl implements GitService {
    private final Git git;
    private final File configDir;
    private final Map<String, Instant> pulls = new HashMap<>();

    public static GitService init(File localDir, String remoteUrl) {
        try {
            var git = localDir.exists() ? useLocal(localDir) : cloneRemote(remoteUrl, localDir);
            return new GitServiceImpl(git, git.getRepository().getDirectory().getParentFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Git useLocal(File localDir) throws IOException {
        return new Git(new FileRepository(new File(localDir, "/.git")));
    }

    private static Git cloneRemote(String remoteUrl, File localDir) throws GitAPIException {
        return Git.cloneRepository()
            .setURI(remoteUrl)
            .setDirectory(localDir)
            .call();
    }

    @Override
    public synchronized File checkout(String branchName) {
        try {
            checkoutBranch(branchName);
            pullBranch(branchName);
            return configDir;
        } catch (RefNotFoundException e) {
            throw new BranchNotFoundException(branchName);
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkoutBranch(String branchName) throws GitAPIException, IOException {
        if (git.getRepository().findRef(branchName) == null) {
            createBranch(branchName);
        }
        git.checkout().setName(branchName).call();
    }

    private void pullBranch(String branch) throws GitAPIException {
        var now = Instant.now();
        if (pulls.computeIfAbsent(branch, __ -> now.minusSeconds(10)).isBefore(now)) {
            log.debug("Pulling branch {}", branch);
            git.pull().call();
            pulls.put(branch, now.plusSeconds(10));
        }
    }

    private void createBranch(String name) throws GitAPIException {
        log.debug("Creating and pulling branch {}", name);
        git.branchCreate()
            .setName(name)
            .setStartPoint("origin/" + name)
            .call();
    }

}
