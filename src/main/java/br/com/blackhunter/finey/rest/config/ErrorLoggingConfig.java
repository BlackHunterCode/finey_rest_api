/*
 * @(#)ErrorLoggingConfig.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Alterações somente por CODEOWNERS.
 * 
 * <p>Configuration for error logging transaction management.
 * Uses a separate transaction manager to ensure error logging works even when
 * the main transaction is marked for rollback.</p>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "br.com.blackhunter.hunter_wallet.rest_api.core.trace.repository",
        "br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository",
        "br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.repository",
        "br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository"
    },
    transactionManagerRef = "errorLoggingTransactionManager"
)
@EntityScan(
    basePackages = {
        "br.com.blackhunter.hunter_wallet.rest_api.core.trace.entity",
        "br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity",
        "br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.entity",
        "br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity"
    }
)
public class ErrorLoggingConfig {

    @Bean
    @Primary
    public PlatformTransactionManager errorLoggingTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setNestedTransactionAllowed(true);
        return transactionManager;
    }
}
