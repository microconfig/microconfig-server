package io.microconfig.server;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class ServerMain {
    public static void main(String[] args) {
        SpringApplication.run(ServerMain.class, args);
    }

    @Bean
    public VaultConfig vault(@Value("${vault.address}") String address) throws VaultException {
        return new VaultConfig()
            .address(address)
            .engineVersion(2)
            .build();
    }
}
