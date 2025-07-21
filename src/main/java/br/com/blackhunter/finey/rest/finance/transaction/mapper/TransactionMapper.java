/*
 * @(#)TransactionMapper.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.finance.transaction.mapper;

import br.com.blackhunter.finey.rest.finance.transaction.dto.TransactionData;
import br.com.blackhunter.finey.rest.finance.transaction.entity.TransactionEntity;
import br.com.blackhunter.finey.rest.finance.transaction.payload.TransactionPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * <p>Interface <code>TransactionMapper</code>.</p>
 * <p>Interface de mapeamentos de contas de usuário.</p>
 * */
@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "openBankingAccountId", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    @Mapping(target = "providerTransactionCode", ignore = true)
    @Mapping(target = "providerTransactionId", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "descriptionRaw", source = "descriptionRaw")
    @Mapping(target = "currencyCode", source = "currencyCode")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "type", source = "type")
    @Mapping(
            target = "status",
            expression = "java(payload.isApproved() ? br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionStatus.POSTED : br.com.blackhunter.finey.rest.finance.transaction.enums.TransactionStatus.PENDING)"
    )
    @Mapping(target = "transactionDate", source = "transactionDate")
    TransactionEntity toEntity(TransactionPayload payload);

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "descriptionRaw", source = "descriptionRaw")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "currencyCode", source = "currencyCode")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "transactionDate", source = "transactionDate")
    TransactionData toData(TransactionEntity entity);
}