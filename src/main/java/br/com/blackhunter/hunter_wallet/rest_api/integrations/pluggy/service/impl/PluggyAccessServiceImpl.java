/*
 * @(#)PluggyAccessServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.impl;

import br.com.blackhunter.hunter_wallet.rest_api.auth.util.CryptUtil;
import br.com.blackhunter.hunter_wallet.rest_api.auth.util.JwtUtil;
import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.blackhunter.hunter_wallet.rest_api.client.PluggyWebClient;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity.PluggyAccessDataEntity;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository.PluggyAccessDataRepository;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;

@Service
public class PluggyAccessServiceImpl implements PluggyAccessService {
    @Value("${hunter.secrets.pluggy.client-id}")
    private String PLUGGY_CLIENT_ID;
    @Value("${hunter.secrets.pluggy.client-secret}")
    private String PLUGGY_CLIENT_SECRET;
    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    private final PluggyWebClient pluggyWebClient;
    private final PluggyAccessDataRepository pluggyAccessDataRepository;

    public PluggyAccessServiceImpl(
            PluggyWebClient pluggyWebClient,
            PluggyAccessDataRepository pluggyAccessDataRepository
    ) {
        this.pluggyWebClient = pluggyWebClient;
        this.pluggyAccessDataRepository = pluggyAccessDataRepository;
    }

    /**
     * <p>Pega o token de acesso a API da pluggy e se necessário ele gera outro token e persiste no banco de dados.</p>
     * <p>O token é salvo criptografado.</p>
     *
     * @return O token de acesso a API da pluggy criptografado.
     * @since 1.0.0
     * @author Victor Barberino
     * */
    @Override
    @Cacheable(key = "'pluggyAccessToken'", value = "accessToken")
    public String getAndSaveAccessTokenEncryptedIfNecessary() {
        String accessToken = null;
        if(!isTokenNotExpiredOrIsTokenExists()) {
            try {
                accessToken = CryptUtil.encrypt(
                        pluggyWebClient.getAccessToken(PLUGGY_CLIENT_ID, PLUGGY_CLIENT_SECRET),
                        PLUGGY_CRYPT_SECRET
                );
                pluggyAccessDataRepository.save(new PluggyAccessDataEntity(accessToken));
            }
            catch (Exception e) {
                throw new BusinessException("Erro ao obter o token de acesso: " + e.getMessage());
            }
        }
        else accessToken = getAccessTokenEncrypted();
        return accessToken;
    }

    public String getAccessTokenEncrypted() {
        PluggyAccessDataEntity accessData = pluggyAccessDataRepository.findAll().stream().findFirst().orElse(null);
        return accessData != null ? accessData.getAccessToken() : null;
    }

    private boolean isTokenNotExpiredOrIsTokenExists() {
        return pluggyAccessDataRepository.alreadyHasRegistration() && isTokenNotExpired(getAccessTokenEncrypted());
    }

    private boolean isTokenNotExpired(String accessTokenEncrypted) {
        return pluggyWebClient.testAccessToken(accessTokenEncrypted);
    }
}
