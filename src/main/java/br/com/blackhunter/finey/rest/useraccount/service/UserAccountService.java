/*
 * @(#)UserAccountService.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.useraccount.service;

import br.com.blackhunter.finey.rest.useraccount.dto.UserAccountData;
import br.com.blackhunter.finey.rest.useraccount.dto.projections.UserInfoDataProjected;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.finey.rest.useraccount.payload.UserAccountPayload;

import java.util.UUID;

public interface UserAccountService {
    /* Serviços expostos como endpoints */
    UserAccountData registerUser(UserAccountPayload reqPayload);
    UserAccountData authenticateUser(Object loginDTO);
    UserInfoDataProjected getUserInfoByAuthToken();

    /* Não expostos como endpoint (usados internamente) */
    UserAccountData findById(UUID userId);
    UserAccountEntity findEntityById(UUID userId);
    UUID findUserIdByEmail(String email);
    void verifyEmail(String verificationToken);
    void initiatePasswordReset(String email);
    void completePasswordReset(String token, String newPassword);
    void deactivateAccount(UUID userId);
}
