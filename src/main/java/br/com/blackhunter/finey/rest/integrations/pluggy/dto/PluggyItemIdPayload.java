package br.com.blackhunter.finey.rest.integrations.pluggy.dto;

import br.com.blackhunter.finey.rest.core.annotations.Encrypted;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluggyItemIdPayload {
    @NotBlank(message = "The \"itemId\" field is mandatory.")
    @Encrypted
    private String itemId;

    @NotBlank(message = "The \"connectorId\" field is mandatory.")
    @Encrypted
    private String connectorId;

    @NotBlank(message = "The \"imageUrl\" field is mandatory.")
    @Encrypted
    private String imageUrl;

    @NotBlank(message = "The \"name\" field is mandatory.")
    @Encrypted
    private String name;
}
