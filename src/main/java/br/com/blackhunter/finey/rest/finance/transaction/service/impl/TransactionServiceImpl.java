/*
 * @(#)TransactionServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.finance.transaction.service.impl;

import br.com.blackhunter.finey.rest.auth.util.JwtUtil;
import br.com.blackhunter.finey.rest.finance.transaction.dto.TransactionData;
import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.finance.transaction.mapper.TransactionMapper;
import br.com.blackhunter.finey.rest.finance.transaction.payload.TransactionPayload;
import br.com.blackhunter.finey.rest.finance.transaction.repository.TransactionRepository;
import br.com.blackhunter.finey.rest.finance.transaction.service.TransactionService;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import org.springframework.validation.annotation.Validated;

/**
 * <p>Classe <code>TransactionServiceImpl</code>.</p>
 * <p>Essa classe implementa o processamento e as regras de negócio dos serviços de transações</p>
 * */
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final JwtUtil jwtUtil;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            JwtUtil jwtUtil
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper     = transactionMapper;
        this.jwtUtil               = jwtUtil;
    }

    /**
     * @param transactionPayload payload contendo os dados da transação a ser registrada.
     * <p>Esse método registra uma transação no banco de dados.</p>
     * <p>Uma transação pode ser um gasto ou um ganho.</p>
     * <p>
     *   Obs: Esse método vai ser mais usado em um evento de <code>Webhook</code>
     *   ou manualmente pelo usuário através da <code>API REST</code>.
     * </p>
     *
     * @author Victor Barberino
     * @return Retorna os dados da transação registrada.
     * */
    @Override
    public TransactionData registerTransaction(@Validated TransactionPayload transactionPayload) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(transactionPayload);
        UserAccountEntity userAccountEntity =  jwtUtil.getUserAccountFromToken();
        transactionEntity.setUserAccount(userAccountEntity);
        return transactionMapper.toData(transactionRepository.save(transactionEntity));
    }
}
