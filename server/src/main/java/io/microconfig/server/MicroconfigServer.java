package io.microconfig.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableScheduling
@SpringBootApplication
public class MicroconfigServer {
    public static void main(String[] args) {
        run(MicroconfigServer.class, args);
    }
}