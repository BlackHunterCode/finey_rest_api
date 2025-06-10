package br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.repository;

import br.com.blackhunter.hunter_wallet.rest_api.finance.transaction.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}
