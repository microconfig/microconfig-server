package io.microconfig.server.git;

import java.io.File;

public interface GitService {

    File checkoutDefault();
    File checkout(String branch);
}
