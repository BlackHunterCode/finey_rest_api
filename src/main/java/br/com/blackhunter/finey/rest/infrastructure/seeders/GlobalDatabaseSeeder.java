/*
 * @(#)GlobalDatabaseSeeder.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.infrastructure.seeders;

import br.com.blackhunter.finey.rest.core.util.AppContextUtil;
import br.com.blackhunter.finey.rest.infrastructure.persistence.DomainSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@Slf4j
public class GlobalDatabaseSeeder implements ApplicationRunner {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final List<DomainSeeder> seeders;

    public GlobalDatabaseSeeder(List<DomainSeeder> seeders) {
        this.seeders = seeders;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!AppContextUtil.isDevelopmentMode(activeProfile)) {
            return;
        }

        log.info("Running global database seeders...");
        seeders.forEach(seeder -> {
            if (seeder.shouldSeed()) {
                seeder.seed();
            }
        });
        log.info("Global database seeders completed.");
    }
}
