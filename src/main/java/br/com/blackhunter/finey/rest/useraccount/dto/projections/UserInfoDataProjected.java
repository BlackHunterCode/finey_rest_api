package br.com.blackhunter.finey.rest.useraccount.dto.projections;

import br.com.blackhunter.finey.rest.integrations.financial_integrator.dto.FinancialInstitutionData;
import br.com.blackhunter.finey.rest.useraccount.enums.UserAccountStatus;
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
    private List<FinancialInstitutionData> connectedBanks;

    public UserInfoDataProjected(UserInfoData userInfoData, List<FinancialInstitutionData> connectedBanks) {
        this.accountId = userInfoData.getAccountId();
        this.fullName = userInfoData.getFullName();
        this.firstName = userInfoData.getFirstName();
        this.lastName = userInfoData.getLastName();
        this.endDateTimeOfTutorialPeriod = userInfoData.getEndDateTimeOfTutorialPeriod();
        this.accountStatus = userInfoData.getAccountStatus();
        this.connectedBanks = connectedBanks;
    }
}
