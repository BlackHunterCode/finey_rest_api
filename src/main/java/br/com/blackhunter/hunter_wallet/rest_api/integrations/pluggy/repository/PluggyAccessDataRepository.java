/*
 * @(#)PluggyAccessDataRepository.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.repository;

import br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity.PluggyAccessDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PluggyAccessDataRepository extends JpaRepository<PluggyAccessDataEntity, UUID> {
    @Query("SELECT COUNT(*) > 0 FROM UserAccountEntity u")
    boolean alreadyHasRegistration();
}
