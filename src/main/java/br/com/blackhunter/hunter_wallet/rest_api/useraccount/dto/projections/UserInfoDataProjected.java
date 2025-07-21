package br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.projections;

import br.com.blackhunter.hunter_wallet.rest_api.useraccount.enums.UserAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDataProjected {
    private UUID accountId;
    private String fullName;
    private String firstName;
    private String lastName;
    private LocalDateTime endDateTimeOfTutorialPeriod;
    private UserAccountStatus accountStatus;
    private List<String> connectedBanks;

    public UserInfoDataProjected(UserInfoData userInfoData, List<String> connectedBanks) {
        this.accountId = userInfoData.getAccountId();
        this.fullName = userInfoData.getFullName();
        this.firstName = userInfoData.getFirstName();
        this.lastName = userInfoData.getLastName();
        this.endDateTimeOfTutorialPeriod = userInfoData.getEndDateTimeOfTutorialPeriod();
        this.accountStatus = userInfoData.getAccountStatus();
        this.connectedBanks = connectedBanks;
    }
}
