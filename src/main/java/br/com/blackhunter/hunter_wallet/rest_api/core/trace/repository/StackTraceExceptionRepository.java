package br.com.blackhunter.hunter_wallet.rest_api.core.trace.repository;

import br.com.blackhunter.hunter_wallet.rest_api.core.trace.entity.StackTraceExceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StackTraceExceptionRepository extends JpaRepository<StackTraceExceptionEntity, UUID> {
}
