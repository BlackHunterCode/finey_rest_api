package br.com.blackhunter.finey.rest.integrations.pluggy.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyItemEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PluggyItemRepository extends JpaRepository<PluggyItemEntity, UUID> {
    Optional<PluggyItemEntity> findByConnectorId(String connectorId);

    @Query("SELECT p FROM PluggyItemEntity p WHERE p.userAccount.accountId = :userId")
    List<PluggyItemEntity> findAllItemsByUserId(@Param("userId") UUID userId);
}
