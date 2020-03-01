package io.microconfig.server.git;

public class BranchNotFoundException extends RuntimeException {
    public BranchNotFoundException(String branch) {
        super("Not found branch " + branch);
    }
}
