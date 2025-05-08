/**
 * 2025 © Black Hunter - Todos os Direitos Reservados.
 *
 * Classe protegida - Aletrações somente por CODEOWNERS.
 * */

package br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto;

import lombok.Data;

import java.util.UUID;

/**
 * <p>Classe <code>UserAccountData</code>.</p>
 * <p>Classe DTO usada para transferir dados da conta de usuário pela aplicação.</p>
 * <p><strong>NÃO É RECOMENDADO</strong> usar essa classe para retornos de endpoints.</p>
 * <p>A classe adequada para requisições e respostas é UserAccountPayload</p>
 *
 * @see br.com.blackhunter.hunter_wallet.rest_api.useraccount.payload.UserAccountPayload
 * */
@Data
public class UserAccountData {
    private UUID accountId;
    private String accountName;
    private String accountUsername;
    private String subscriptionType;
    // Outros dados.

    /**
     * Construtor padrão da classe.
     * */
    public UserAccountData() {

    }
}
