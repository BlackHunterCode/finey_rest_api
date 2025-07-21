/*
 * @(#)PluggyAccessServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.blackhunter.hunter_wallet.rest_api.auth.util.CryptUtil;
import br.com.blackhunter.hunter_wallet.rest_api.auth.util.JwtUtil;
import br.com.blackhunter.hunter_wallet.rest_api.client.PluggyWebClient;
import br.com.blackhunter.hunter_wallet.rest_api.core.exception.BusinessException;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdPayload;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdResponse;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity.PluggyAccessDataEntity;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity.PluggyItemEntity;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository.PluggyAccessDataRepository;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository.PluggyItemRepository;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service.PluggyAccessService;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PluggyAccessServiceImpl implements PluggyAccessService {
    @Value("${hunter.secrets.pluggy.client-id}")
    private String PLUGGY_CLIENT_ID;
    @Value("${hunter.secrets.pluggy.client-secret}")
    private String PLUGGY_CLIENT_SECRET;
    @Value("${hunter.secrets.pluggy.crypt-secret}")
    private String PLUGGY_CRYPT_SECRET;

    private final JwtUtil jwtUtil;
    private final PluggyWebClient pluggyWebClient;
    private final PluggyAccessDataRepository pluggyAccessDataRepository;
    private final PluggyItemRepository pluggyItemRepository;

    public PluggyAccessServiceImpl(
            JwtUtil jwtUtil,
            PluggyWebClient pluggyWebClient,
            PluggyAccessDataRepository pluggyAccessDataRepository,
            PluggyItemRepository pluggyItemRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.pluggyWebClient = pluggyWebClient;
        this.pluggyAccessDataRepository = pluggyAccessDataRepository;
        this.pluggyItemRepository = pluggyItemRepository;
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
    public String getAndSaveAccessTokenEncryptedIfNecessary() {
        String accessToken = null;
        System.out.println("Condicao: " + isTokenNotExistsOrIsTokenExpired());
        log.info("Condicao: {}", isTokenNotExistsOrIsTokenExpired());
        if(isTokenNotExistsOrIsTokenExpired()) {
            try {
                String rawtoken = pluggyWebClient.getAccessToken(PLUGGY_CLIENT_ID, PLUGGY_CLIENT_SECRET);
                accessToken = CryptUtil.encrypt(rawtoken, PLUGGY_CRYPT_SECRET);
                pluggyAccessDataRepository.deleteAll(); // limpo a tabela novamente.
                pluggyAccessDataRepository.save(new PluggyAccessDataEntity(accessToken));
            }
            catch (Exception e) {
                throw new BusinessException("Error getting access token: " + e.getMessage());
            }
        }
        else accessToken = Objects.requireNonNull(getAccessTokenEncrypted()).getAccessToken();

        return accessToken;
    }

    /**
     * @param token O token de acesso criptografado.
     * <p>Busca o connect token da API da Pluggy.</p>
     * <p>
     *     O connect token tem uma duração máxima de expiração de 30 minutos,
     *     e é usado somente para conectar as credenciais bancárias do usuário.
     * </p>
     * <p>
     *     Por isso não persistimos o dado no cache ou no banco de dados.
     *     Então considere um tempo de resposta desse método.
     * </p>
     *
     * @return connect token criptografado
     * */
    @Override
    public String getConnectTokenEncrypted(String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
     
            String accessToken = CryptUtil.decrypt(token, PLUGGY_CRYPT_SECRET);
            String connectToken = pluggyWebClient.getConnectToken(accessToken);
            if (connectToken == null || connectToken.isEmpty()) {
                throw new IllegalStateException("Received empty connect token from Pluggy API");
            }
            
            return CryptUtil.encrypt(connectToken, PLUGGY_CRYPT_SECRET);
        }
        catch (Exception e) {
            String errorMsg = "Error getting connection token: " + e.getMessage();
            throw new BusinessException(errorMsg);
        }
    }

    @Override
    public PluggyItemIdResponse savePluggyItemId(PluggyItemIdPayload payload) {
        try {
            UserAccountEntity user = jwtUtil.getUserAccountFromToken();
            if (user == null) {
                throw new BusinessException("User not found");
            }

            PluggyItemEntity itemToSave = new PluggyItemEntity(
                    null,
                    payload.getItemId(),
                    String.valueOf(payload.getConnectorId()),
                    payload.getImageUrl(),
                    payload.getName(),
                    user,
                    LocalDateTime.now()
            );
            Optional<PluggyItemEntity> optionalPluggyItemEntity = getPluggyItemByConnectorId(payload.getConnectorId());
            if(optionalPluggyItemEntity.isPresent()) {
                itemToSave = optionalPluggyItemEntity.get();
                itemToSave.setOriginalPluggyItemId(payload.getItemId());
            }

            return new PluggyItemIdResponse(pluggyItemRepository.save(itemToSave).getItemId());
        } catch (Exception e) {
            throw new BusinessException("Error getting Pluggy item ID: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAllItemsByUserId(UUID userId) {
        return pluggyItemRepository.findAllItemsByUserId(userId)
                .stream()
                .map(UUID::toString)
                .toList();
    }

    /* Métodos privados */

    private PluggyAccessDataEntity getAccessTokenEncrypted() {
        PluggyAccessDataEntity accessData = pluggyAccessDataRepository.findAll().stream().findFirst().orElse(null);
        return accessData != null ? accessData : null;
    }

    private Optional<PluggyItemEntity> getPluggyItemByConnectorId(String connectorId) {
        return pluggyItemRepository.findByConnectorId(connectorId);
    }

    private boolean isTokenNotExistsOrIsTokenExpired() {
        try {
            return !pluggyAccessDataRepository.alreadyHasRegistration() || isTokenExpired(getAccessTokenEncrypted());
        } catch (Exception e) {
            throw new BusinessException("Error verifying access token: " + e.getMessage());
        }
    }

    private boolean isTokenExpired(PluggyAccessDataEntity accessData) throws Exception {
        if (accessData == null || accessData.getAccessToken() == null || accessData.getAccessToken().isEmpty()) {
            return true;
        }

        LocalDateTime tokenExpirationTime = accessData.getObtainedAt().plusHours(1).plusMinutes(30);
        return tokenExpirationTime.isBefore(LocalDateTime.now());
    }
}
