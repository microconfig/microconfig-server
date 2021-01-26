package io.microconfig.spring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Starter {
    public static void main(String[] args) {
        run(Starter.class, args);
    }

    @Configuration
    public static class Config {
        @Bean
        public String micro(@Value("micro") String micro) {
            System.out.println(micro);
            return "";
        }
    }
}