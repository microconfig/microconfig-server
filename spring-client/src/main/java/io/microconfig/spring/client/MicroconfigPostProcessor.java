package io.microconfig.spring.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Order
public class MicroconfigPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("called post");
        var name = environment.getProperty("spring.application.name");
        if (name == null) return;
        System.out.println("App name " + name);
        environment.getPropertySources().addLast(new MicroconfigPropertySource());
    }
}