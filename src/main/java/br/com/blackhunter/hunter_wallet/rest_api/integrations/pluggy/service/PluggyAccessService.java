/*
 * @(#)PluggyAccessService.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.service;

public interface PluggyAccessService {
    String getAndSaveAccessTokenIfNecessary();
}
