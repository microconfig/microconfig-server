package io.microconfig.server.git;

import lombok.Getter;
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
public class GitService {
    private final String currentBranch = "master";
    private final Git git;

    public void checkOutCurrentBranch() {
        try {
            git.checkout().setName(currentBranch).call();
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
