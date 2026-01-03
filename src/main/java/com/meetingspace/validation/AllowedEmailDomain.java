package com.meetingspace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedEmailDomainValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedEmailDomain {

    /**
     * Allowed domain (root only, e.g., "hcltech.com").
     * If you need more than one, you can change type to String[] later,
     * but for now we keep it simple with a single domain.
     */
    String value();

    String message() default "Email must be an @hcltech.com address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
