package br.com.blackhunter.finey.rest.integrations.pluggy.dto;

import br.com.blackhunter.finey.rest.auth.util.CryptUtil;
import br.com.blackhunter.finey.rest.integrations.pluggy.entity.PluggyAccountDataEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluggyAccountIds {
    private String accountId;
    private String type;
    private String balance;

    public static PluggyAccountIds fromEntity(PluggyAccountDataEntity entity, final String PLUGGY_SECRET_KEY ) throws Exception {
        return new PluggyAccountIds(
            CryptUtil.encrypt(String.valueOf(entity.getPluggyAccountId()), PLUGGY_SECRET_KEY),
            entity.getPluggyAccountType(),
            entity.getPluggyAccountBalance()
        );
    }
}
