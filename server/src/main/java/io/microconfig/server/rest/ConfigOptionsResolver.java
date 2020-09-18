package io.microconfig.server.rest;

import io.microconfig.server.configs.ConfigOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class ConfigOptionsResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ConfigOptions.class == parameter.getParameterType();
    }

    @Override
    public ConfigOptions resolveArgument(MethodParameter _1,
                                         ModelAndViewContainer _2,
                                         NativeWebRequest webRequest,
                                         WebDataBinderFactory _3) {
        var branch = webRequest.getHeader("X-BRANCH");
        var tag = webRequest.getHeader("X-TAG");
        var vars = vars(webRequest);
        return new ConfigOptions(branch, tag, vars);
    }

    private Map<String, String> vars(NativeWebRequest webRequest) {
        var headerValues = webRequest.getHeaderValues("X-VAR");
        if (headerValues == null) return emptyMap();
        return stream(headerValues)
                .map(ConfigOptionsResolver::splitVar)
                .collect(toMap(s -> s[0], s -> s[1]));
    }

    private static String[] splitVar(String str) {
        int idx = str.indexOf('=');
        return new String[]{str.substring(0, idx), str.substring(idx + 1)};
    }
}
