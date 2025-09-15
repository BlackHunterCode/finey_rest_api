/*
 * @(#)PluggyAccessServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.integrations.pluggy.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.FinancialInstitutionData;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyAccountIds;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyAccountDataEntity;
import br.com.blackhunter.finey.rest.integrations.pluggy.repository.PluggyAccountDataRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.auth.util.JwtUtil;
import br.com.blackhunter.finey.rest.client.PluggyWebClient;
import br.com.blackhunter.finey.rest.core.exception.BusinessException;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyItemIdPayload;
import br.com.blackhunter.finey.rest.integrations.pluggy.dto.PluggyItemIdResponse;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyAccessDataEntity;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyItemEntity;
import br.com.blackhunter.finey.rest.integrations.pluggy.repository.PluggyAccessDataRepository;
import br.com.blackhunter.finey.rest.integrations.pluggy.repository.PluggyItemRepository;
import br.com.blackhunter.finey.rest.integrations.pluggy.service.PluggyAccessService;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
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
    private final PluggyAccountDataRepository pluggyAccountDataRepository;

    public PluggyAccessServiceImpl(
            JwtUtil jwtUtil,
            PluggyWebClient pluggyWebClient,
            PluggyAccessDataRepository pluggyAccessDataRepository,
            PluggyItemRepository pluggyItemRepository,
            PluggyAccountDataRepository pluggyAccountDataRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.pluggyWebClient = pluggyWebClient;
        this.pluggyAccessDataRepository = pluggyAccessDataRepository;
        this.pluggyItemRepository = pluggyItemRepository;
        this.pluggyAccountDataRepository = pluggyAccountDataRepository;
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
    @Transactional(rollbackOn = {BusinessException.class, Exception.class})
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

                // limpa todas as contas que estão associadas a esse item, para atualizar novamente
                pluggyAccountDataRepository.deleteAllByOriginalPluggyItemId(CryptUtil.decrypt(payload.getItemId(), PLUGGY_CRYPT_SECRET));
            }

            PluggyItemEntity itemSaved = pluggyItemRepository.save(itemToSave);

            // hora de buscar os ids das contas
            List<PluggyAccountDataEntity> accountIds = new ArrayList<>();

            for(PluggyWebClient.PluggyAccount ac : pluggyWebClient.getAccountIdsByItemId(CryptUtil.decrypt(payload.getItemId(), PLUGGY_CRYPT_SECRET), CryptUtil.decrypt(getAndSaveAccessTokenEncryptedIfNecessary(), PLUGGY_CRYPT_SECRET))) {
                PluggyAccountDataEntity pad = new PluggyAccountDataEntity(
                        null,
                        itemToSave,
                        Set.of(),
                        CryptUtil.encrypt(ac.getId(), PLUGGY_CRYPT_SECRET),
                        CryptUtil.encrypt(ac.getName(), PLUGGY_CRYPT_SECRET),
                        CryptUtil.encrypt(ac.getType(), PLUGGY_CRYPT_SECRET),
                        CryptUtil.encrypt(String.valueOf(BigDecimal.valueOf(ac.getBalance())), PLUGGY_CRYPT_SECRET ),
                        LocalDateTime.now()
                );
                accountIds.add(pad);
            }

            // salva as contas associadas ao item
            if(accountIds.isEmpty()) {
                // lanço um erro porque não deveria acontecer de não ter contas associadas ao item.
                // o rollback vai acontecer automaticamente.
                throw new BusinessException("No accounts found for the provided item ID.");
            }

            System.out.println("Saving accounts: " + accountIds);
            pluggyAccountDataRepository.saveAll(accountIds);

            return new PluggyItemIdResponse(itemSaved.getItemId());
        } catch (Exception e) {
            throw new BusinessException("Error getting Pluggy item ID: " + e.getMessage());
        }
    }

    @Override
    public List<FinancialInstitutionData> getAllItemsByUserId(UUID userId) {
        return pluggyItemRepository.findAllItemsByUserId(userId)
            .stream()
            .map(item -> {
                try {
                    return new FinancialInstitutionData(
                            item.getItemId().toString(),
                            item.getName(),
                            item.getImageUrl(),
                            pluggyAccountDataRepository.findAllByItemId(item.getItemId())
                                    .stream().map(pa -> {
                                        try {
                                            return PluggyAccountIds.fromEntity(pa, PLUGGY_CRYPT_SECRET);
                                        } catch (Exception e) {
                                            throw new BusinessException(e.getMessage());
                                        }
                                    }).toList()
                    );
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                }
            })
            .toList();
    }

    @Override
    public String getOriginalPluggyAccountIdByEntityId(UUID entityId) {
        return pluggyAccountDataRepository.findById(entityId)
                .map(PluggyAccountDataEntity::getPluggyOriginalAccountId)
                .orElseThrow(() -> new BusinessException("Pluggy account not found for entity ID: " + entityId));
    }

    /**
     * Esse método busca as transações de um período específico DIRETO da API da pluggy.
     * */
    @Override
    public List<TransactionEntity> getAllTransactionsPeriodByOriginalAccountId(final String originalAccountId, final LocalDate startDate, final LocalDate endDate) {
        UserAccountEntity user = jwtUtil.getUserAccountFromToken();
        PluggyAccountDataEntity pluggyAccountData = pluggyAccountDataRepository.findByPluggyOriginalAccountId(originalAccountId)
                .orElseThrow(() -> new BusinessException("Pluggy account not found for original account ID: " + originalAccountId));

        try {
            return pluggyWebClient.getAllTransactionsPeriodByOriginalAccountId(
                    CryptUtil.decrypt(getAndSaveAccessTokenEncryptedIfNecessary(), PLUGGY_CRYPT_SECRET),
                    CryptUtil.decrypt(originalAccountId, PLUGGY_CRYPT_SECRET),
                            startDate,
                            endDate)
                    .stream()
                    .map(t -> {
                        TransactionEntity entity = t.toTransactionEntity();
                        entity.setUserAccount(user);
                        entity.setPluggyAccountId(pluggyAccountData);
                        return entity;
                    }).toList();
        } catch (Exception e) {
            throw new BusinessException("Error fetching transactions from pluggy API:" + e.getMessage());
        }
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
