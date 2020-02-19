package io.microconfig.server.git;

import java.io.File;

public interface GitService {
    void checkout(String branch);

    File getLocalDir();
}
