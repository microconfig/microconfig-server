package io.microconfig.server;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableScheduling
@SpringBootApplication
public class MicroConfigServerStarter {
    public static void main(String[] args) {
        run(MicroConfigServerStarter.class, args);
    }

    @Bean
    public VaultConfig vault(@Value("${vault.address}") String address) throws VaultException {
        return new VaultConfig()
                .address(address)
                .engineVersion(2)
                .build();
    }
}
