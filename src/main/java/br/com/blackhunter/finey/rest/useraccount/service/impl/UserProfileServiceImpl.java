/*
 * @(#)UserProfileServiceImpl.java
 *
 * Copyright 2025, Black Hunter
 * http://www.blackhunter.com.br
 *
 * Todos os direitos reservados.
 */

package br.com.blackhunter.finey.rest.useraccount.service.impl;

import br.com.blackhunter.finey.rest.core.exception.BusinessException;
import br.com.blackhunter.finey.rest.core.util.MultipartFileUtil;
import br.com.blackhunter.finey.rest.useraccount.dto.UserProfileData;
import br.com.blackhunter.finey.rest.useraccount.entity.UserAccountEntity;
import br.com.blackhunter.finey.rest.useraccount.entity.UserProfileEntity;
import br.com.blackhunter.finey.rest.useraccount.mapper.UserProfileMapper;
import br.com.blackhunter.finey.rest.useraccount.payload.UserProfilePayload;
import br.com.blackhunter.finey.rest.useraccount.repository.UserProfileRepository;
import br.com.blackhunter.finey.rest.useraccount.service.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository repository;
    private final UserProfileMapper mapper;

    public UserProfileServiceImpl(
            UserProfileRepository repository,
            UserProfileMapper mapper
    ) {
        this.repository = repository;
        this.mapper     = mapper;
    }

    @Override
    @NonNull
    public UserProfileData createProfile(@NonNull UserAccountEntity userAccount, @NonNull UserProfilePayload payload) {
        // Explicit null checks to throw BusinessException
        if (userAccount == null) {
            throw new BusinessException("UserAccountEntity cannot be null");
        }
        if (payload == null) {
            throw new BusinessException("UserProfilePayload cannot be null");
        }
        
        UserProfileEntity toSave = mapper.toEntity(payload);
        if(payload.getProfilePictureFile() != null) {
            toSave.setProfilePictureFile(MultipartFileUtil.validateAndGetMultipartFileBytes(
                    payload.getProfilePictureFile()
            ));
        }
        toSave.setUserAccount(userAccount);
        UserProfileEntity profile = repository.save(toSave);
        return mapper.toData(profile);
    }

    @Override
    @NonNull
    public UserProfileData updateProfile(@NonNull UUID profileId, @NonNull UserProfilePayload payload) {
        UserProfileEntity profile = findByUserAccountId(profileId);
        mapper.updateEntity(profile, payload);
        if (payload.getProfilePictureFile() != null) {
            profile.setProfilePictureFile(MultipartFileUtil.validateAndGetMultipartFileBytes(payload.getProfilePictureFile()));
        }
        return mapper.toData(repository.save(profile));
    }

    @Override
    @NonNull
    public UserProfileEntity findByUserAccountId(@NonNull UUID userId) {
        return repository.findByAccountId(userId).orElseThrow(
            () -> new EntityNotFoundException("The user profile provided does not exist.")
        );
    }
}
