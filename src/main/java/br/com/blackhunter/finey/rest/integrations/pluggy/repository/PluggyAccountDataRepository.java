package br.com.blackhunter.finey.rest.integrations.pluggy.repository;

import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyAccountDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PluggyAccountDataRepository extends JpaRepository<PluggyAccountDataEntity, UUID> {
    @Modifying
    @Query("DELETE FROM PluggyAccountDataEntity pad WHERE pad.itemId.originalPluggyItemId = :itemId")
    void deleteAllByOriginalPluggyItemId(@Param("itemId") String itemId);

    @Query("SELECT pad FROM PluggyAccountDataEntity pad WHERE pad.itemId.itemId = :itemId")
    List<PluggyAccountDataEntity> findAllByItemId(@Param("itemId") UUID itemId);

    @Query("SELECT pad FROM PluggyAccountDataEntity pad WHERE pad.pluggyOriginalAccountId = :pluggyOriginalAccountId")
    Optional<PluggyAccountDataEntity> findByPluggyOriginalAccountId(@Param("pluggyOriginalAccountId") String pluggyOriginalAccountId);
}
