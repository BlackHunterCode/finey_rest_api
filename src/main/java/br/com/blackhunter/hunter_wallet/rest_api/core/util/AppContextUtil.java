/*
 * @(#)AppContextUtil.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.core.util;

public class AppContextUtil {
    /**
     * <p>Verifica se a aplicação está rodando em modo de desenvolvimento.</p>
     *
     * @return true se estiver em modo de desenvolvimento, false caso contrário
     */
    public static boolean isDevelopmentMode(String activeProfile) {
        return activeProfile.contains("dev") || activeProfile.contains("local");
    }
}
