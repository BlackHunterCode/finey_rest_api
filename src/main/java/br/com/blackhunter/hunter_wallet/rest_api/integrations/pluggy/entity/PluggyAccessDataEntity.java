/*
 * @(#)PluggyAccessDataEntity.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "hw_pluggy_access_data")
@Data
public class PluggyAccessDataEntity {
    private UUID accessId;
    @Lob
    @Column(name = "access_token", unique = true)
    private String accessToken;
}
