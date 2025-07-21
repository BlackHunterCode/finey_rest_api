package br.com.blackhunter.finey.rest.core.trace.repository;

import br.com.blackhunter.finey.rest.core.trace.entity.StackTraceExceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StackTraceExceptionRepository extends JpaRepository<StackTraceExceptionEntity, UUID> {
}
