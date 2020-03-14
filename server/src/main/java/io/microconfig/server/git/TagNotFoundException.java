package io.microconfig.server.git;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String tag) {
        super(tag + " tag not found");
    }
}
