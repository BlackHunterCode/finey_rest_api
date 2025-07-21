package br.com.blackhunter.finey.rest.integrations.pluggy.dto;

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
