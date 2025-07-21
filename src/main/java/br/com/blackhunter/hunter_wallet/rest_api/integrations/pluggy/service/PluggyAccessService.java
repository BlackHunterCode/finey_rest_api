/*
 * @(#)PluggyAccessService.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdPayload;
import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto.PluggyItemIdResponse;

import java.util.List;
import java.util.UUID;

public interface PluggyAccessService {
    String getAndSaveAccessTokenEncryptedIfNecessary();
    String getConnectTokenEncrypted(String token);
    PluggyItemIdResponse savePluggyItemId(PluggyItemIdPayload payload);
    List<String> getAllItemsByUserId(UUID userId);
}
