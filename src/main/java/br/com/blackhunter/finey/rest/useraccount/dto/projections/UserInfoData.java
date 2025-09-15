package br.com.blackhunter.finey.rest.useraccount.dto.projections;

import br.com.blackhunter.finey.rest.useraccount.enums.UserAccountStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>Interface Projection <code>UserInfoData</code>.</p>
 * <p>Essa Interface carrega informações gerais sobre o usuário.</p>
 * <p>Não se deve colocar informações sensíveis do usuário nesta classe.</p>
 * */
public interface UserInfoData {
    UUID getAccountId();
    String getFullName();
    String getFirstName();
    String getLastName();
    LocalDateTime getEndDateTimeOfTutorialPeriod();
    UserAccountStatus getAccountStatus();
}
