package br.com.blackhunter.finey.rest.core.validation;

import lombok.Data;

@Data
public class ClientCredentialsValidation {
    public static boolean validateDeviceId(String deviceId) {
        return deviceId != null && !deviceId.isEmpty();
    }

    public static boolean validateAppSignature(String appSignature) {
        return appSignature != null && !appSignature.isEmpty();
    }
}
