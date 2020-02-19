package io.microconfig.server.git;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitServiceImpl implements GitService {
    private final Git git;

    public static GitService init(File localDir, String remoteUrl) {
        try {
            var git = localDir.exists() ? useLocal(localDir) : cloneRemote(remoteUrl, localDir);
            return new GitServiceImpl(git);
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
    public void checkout(String branch) {
        try {
            git.checkout().setName(branch).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getLocalDir() {
        return git.getRepository().getDirectory();
    }

    public List<Ref> refs() {
        try {
            return git.branchList().call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
