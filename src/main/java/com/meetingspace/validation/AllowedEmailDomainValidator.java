package com.meetingspace.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Locale;

public class AllowedEmailDomainValidator implements ConstraintValidator<AllowedEmailDomain, String> {

    private String allowedDomain; // e.g., "hcltech.com"

    @Override
    public void initialize(AllowedEmailDomain constraintAnnotation) {
        // Normalize the domain: lowercase, trimmed
        this.allowedDomain = normalize(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Let @NotBlank/@NotNull handle empties if those annotations are present elsewhere
        if (value == null || value.isBlank()) {
            return true;
        }

        String email = value.trim();

        // Must contain exactly one '@' and local-part must be non-empty
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@')) {
            return false;
        }

        String localPart = email.substring(0, at);
        String domainPart = email.substring(at + 1);

        if (localPart.isEmpty() || domainPart.isEmpty()) {
            return false;
        }

        // Normalize domain: lowercase and trim
        String normalizedDomain = normalize(domainPart);

        // Reject subdomains: must match exactly allowedDomain
        // e.g., "dept.hctech.com" is NOT allowed
        if (!normalizedDomain.equals(allowedDomain)) {
            // Replace default message with a helpful one
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Email must be an @" + allowedDomain + " address"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }
}
