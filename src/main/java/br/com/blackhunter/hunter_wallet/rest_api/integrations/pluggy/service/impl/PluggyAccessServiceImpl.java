/*
 * @(#)PluggyAccessServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.impl;

import br.com.blackhunter.hunter_wallet.rest_api.client.PluggyWebClient;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity.PluggyAccessDataEntity;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository.PluggyAccessDataRepository;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PluggyAccessServiceImpl implements PluggyAccessService {
    @Value("${hunter.secrets.pluggy.client-id}")
    private String PLUGGY_CLIENT_ID;
    @Value("${hunter.secrets.pluggy.client-secret}")
    private String PLUGGY_CLIENT_SECRET;

    private final PluggyWebClient pluggyWebClient;
    private final PluggyAccessDataRepository pluggyAccessDataRepository;

    public PluggyAccessServiceImpl(
            PluggyWebClient pluggyWebClient,
            PluggyAccessDataRepository pluggyAccessDataRepository
    ) {
        this.pluggyWebClient = pluggyWebClient;
        this.pluggyAccessDataRepository = pluggyAccessDataRepository;
    }

    @Override
    public String getAndSaveAccessTokenIfNecessary() {
        if(!isTokenNotExpiredOrIsTokenExists()) {
            String accessToken = pluggyWebClient.getAccessToken(PLUGGY_CLIENT_ID, PLUGGY_CLIENT_SECRET);
            return pluggyAccessDataRepository.save(new PluggyAccessDataEntity(accessToken)).getAccessToken();
        }
        return getAccessToken();
    }

    public String getAccessToken() {
        PluggyAccessDataEntity accessData = pluggyAccessDataRepository.findAll().stream().findFirst().orElse(null);
        return accessData != null ? accessData.getAccessToken() : null;
    }

    private boolean isTokenNotExpiredOrIsTokenExists() {
        return pluggyAccessDataRepository.alreadyHasRegistration() && isTokenNotExpired(getAccessToken());
    }

    private boolean isTokenNotExpired(String accessToken) {
        return pluggyWebClient.testAccessToken(accessToken);
    }
}
