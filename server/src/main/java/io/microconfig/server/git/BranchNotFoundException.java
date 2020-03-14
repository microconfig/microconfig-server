package io.microconfig.server.git;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String branch) {
        super(branch + " branch not found");
    }
}
