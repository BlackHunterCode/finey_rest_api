package br.com.blackhunter.hunter_wallet.rest_api.integrations.pluggy.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluggyItemIdResponse {
    private UUID pluggyItemId;
}
