package br.com.blackhunter.finey.rest.useraccount.service;

import br.com.blackhunter.finey.rest.useraccount.dto.UserProfileData;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.finey.rest.useraccount.entity.UserProfileEntity;
import br.com.blackhunter.finey.rest.useraccount.payload.UserProfilePayload;

import java.util.UUID;

public interface UserProfileService {
    UserProfileData createProfile(UserAccountEntity userAccount, UserProfilePayload profilePayload);
    UserProfileData updateProfile(UUID profileId, UserProfilePayload profilePayload);
    UserProfileEntity findByUserAccountId(UUID userId);
}
