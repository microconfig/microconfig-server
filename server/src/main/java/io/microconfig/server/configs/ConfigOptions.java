package io.microconfig.server.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ConfigOptions {
    @Nullable
    public final String branch;
    @Nullable
    public final String tag;
    @Nullable
    public final Map<String, String> vars;
}
