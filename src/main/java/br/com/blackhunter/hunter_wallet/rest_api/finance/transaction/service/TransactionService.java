/*
 * @(#)TransactionService.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.service;

import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.dto.TransactionData;
import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.payload.TransactionPayload;

public interface TransactionService {
    TransactionData registerTransaction(TransactionPayload transactionPayload);
}
