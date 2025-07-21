package br.com.blackhunter.hunter_wallet.rest_api.auth.validation;

import br.com.blackhunter.hunter_wallet.rest_api.auth.util.CryptUtil;
import br.com.blackhunter.hunter_wallet.rest_api.core.annotations.Encrypted;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class EncryptedValidator implements ConstraintValidator<Encrypted, String> {
    
    @Autowired
    private Environment environment;
    
    private String secretKeyProperty;
    
    @Override
    public void initialize(Encrypted constraintAnnotation) {
        this.secretKeyProperty = constraintAnnotation.secretKeyProperty();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        String secretKey = environment.getProperty(secretKeyProperty);
        return CryptUtil.isEncrypted(value, secretKey);
    }
}
