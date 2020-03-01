package io.microconfig.server.git;

import java.io.File;

public interface GitService {
    File checkout(String branch);
}
