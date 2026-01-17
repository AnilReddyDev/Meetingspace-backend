package com.meetingspace.validation;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AllowedEmailDomainValidatorTest {

    private static Validator validator;

    static class Sample {
        @AllowedEmailDomain("hctech.com")
        String email;
        Sample(String email) { this.email = email; }
    }

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validExactDomain() {
        assertTrue(validator.validate(new Sample("john.doe@hctech.com")).isEmpty());
        assertTrue(validator.validate(new Sample("JOHN@HCTECH.COM")).isEmpty()); // case-insensitive
        assertTrue(validator.validate(new Sample("john@hctech.com ")).isEmpty()); // trims
    }

    @Test
    void rejectSubdomainAndOthers() {
        assertFalse(validator.validate(new Sample("john@dept.hctech.com")).isEmpty());
        assertFalse(validator.validate(new Sample("john@hctech.co")).isEmpty());
        assertFalse(validator.validate(new Sample("john@hctech.com.evil")).isEmpty());
    }

    @Test
    void rejectBadShape() {
        assertFalse(validator.validate(new Sample("john@@hctech.com")).isEmpty());
        assertFalse(validator.validate(new Sample("@hctech.com")).isEmpty());
        assertFalse(validator.validate(new Sample("john@")).isEmpty());
    }
}

