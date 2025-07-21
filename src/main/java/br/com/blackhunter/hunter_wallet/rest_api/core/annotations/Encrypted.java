package br.com.blackhunter.hunter_wallet.rest_api.core.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import br.com.blackhunter.hunter_wallet.rest_api.auth.validation.EncryptedValidator;


@Documented
@Constraint(validatedBy = EncryptedValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypted {
    String message() default "invalid format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Nome da propriedade que contém a chave secreta no application.properties
     */
    String secretKeyProperty() default "hunter.secrets.pluggy.crypt-secret";
}
