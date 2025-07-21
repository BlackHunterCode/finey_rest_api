/*
 * @(#)UserAccountSeeder.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.hunter_wallet.rest_api.infrastructure.seeders;

import br.com.blackhunter.hunter_wallet.rest_api.infrastructure.persistence.DomainSeeder;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.UserAccountData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.dto.UserProfileData;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.payload.UserAccountPayload;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.payload.UserProfilePayload;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.repository.UserAccountRepository;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserAccountService;
import br.com.blackhunter.hunter_wallet.rest_api.useraccount.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class UserAccountSeeder implements DomainSeeder {
    private final UserAccountRepository accountRepository;
    private final UserAccountService accountService;
    private final UserProfileService profileService;


    public UserAccountSeeder(
            UserAccountRepository accountRepository,
            UserAccountService accountService,
            UserProfileService profileService
        ) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.profileService = profileService;
    }

    @Override
    public boolean shouldSeed() {
        return accountRepository.count() == 0;
    }

    @Override
    public void seed() {
        log.info("Seeding user accounts...");
        seedUsers();
        log.info("User accounts seeded successfully.");
    }

    private void seedUsers() {
        UserAccountEntity yui = accountService.findEntityById(
                createUserAccount(
                        "Yui Takashi",
                        "takashi.yui@gmail.com",
                        "DarkLover123",
                        "yuiitakashi"
                ).getAccountId()
        );

        createUserProfile(
                yui,
                "Yui",
                "Takashi",
                "+5511912345678",
                LocalDate.of(2007, 3, 24)
        );

        UserAccountEntity hioda =  accountService.findEntityById(
                createUserAccount(
                "Hioda Takashi",
                "takashi.hioda@gmail.com",
                "Kewan@Forver120",
                "hiodatakashi"
                ).getAccountId()
        );

        createUserProfile(
                hioda,
                "Hioda",
                "Takashi",
                "+5511912345678",
                LocalDate.of(2003, 3, 24)
        );
    }

    private UserAccountData createUserAccount(
            String fullName,
            String email,
            String password,
            String username
    ) {
        UserAccountPayload payload = new  UserAccountPayload(
                fullName,
                email,
                password,
                username
        );

        return accountService.registerUser(payload);
    }

    private UserProfileData createUserProfile(
            UserAccountEntity userAccount,
            String firstName,
            String lastName,
            String phoneNumber,
            LocalDate birthDate
    ) {
        UserProfilePayload payload = new UserProfilePayload(
                firstName,
                lastName,
                phoneNumber,
                birthDate,
                null
        );
        return profileService.createProfile(userAccount, payload);
    }
}
