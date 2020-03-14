package io.microconfig.server.git;

import java.io.File;

public interface GitService {
    File checkoutDefault();

    File checkoutBranch(String branch);

    File checkoutTag(String tag);
}
