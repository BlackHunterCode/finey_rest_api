/*
 * @(#)PluggyStartUpRunner01.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.config.runners;

import br.com.blackhunter.hunter_wallet.rest_api.core.util.DateTimeUtil;
import br.com.blackhunter.hunter_wallet.rest_api.core.util.LogUtil;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Order(1)
public class PluggyStartUpRunner01 implements ApplicationRunner {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String PLUGGY_STARTUP_RUNNER = PluggyStartUpRunner01.class.getName();

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private final PluggyAccessService pluggyAccessService;

    public PluggyStartUpRunner01(PluggyAccessService pluggyAccessService) {
        this.pluggyAccessService = pluggyAccessService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeLogs();
        try
        {
            LogUtil.startSpinner("Obtendo token de acesso do Pluggy");
            String token = pluggyAccessService.getAndSaveAccessTokenEncryptedIfNecessary();
            LogUtil.stopSpinner("Token de acesso obtido com sucesso - " + token , true);
        }
        catch (Exception e)
        {
            LogUtil.stopSpinner("Erro ao obter token de acesso: " + e.getMessage(), false);
            System.exit(1);
        }
        finalizeLogs();
    }

    private void initializeLogs() {
        this.startTime = LocalDateTime.now();
        System.out.println("\n==========================================================");
        System.out.println("               PLUGGY STARTUP RUNNER 01                   ");
        System.out.println("==========================================================\n");

        System.out.println("➡️ Sobre: Testar conexão com o Pluggy e obter o token de acesso.");
        System.out.println("🔄 Status: Iniciando...");
        System.out.println("⏰ Hora de início: " + this.startTime.format(FORMATTER));

        System.out.println("\n----------------------------------------------------------\n");
    }

    private void finalizeLogs() {
        this.endTime = LocalDateTime.now();
        System.out.println("\n----------------------------------------------------------");
        System.out.println("🔄 Finalizando execução...");
        System.out.println("⏳ Tempo total de execução: " + DateTimeUtil.calculateDuration(this.startTime, this.endTime));
        System.out.println("✅ Status: Concluído com sucesso!");
        System.out.println("==========================================================\n");
    }
}