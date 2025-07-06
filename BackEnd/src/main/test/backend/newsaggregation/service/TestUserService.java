package backend.newsaggregation.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class TestUserService {

	private final UserService validator = UserService.getInstance();

    // Positive test cases
    @Test
    public void testValidEmail_simpleFormat() {
        assertTrue(validator.isValidEmail("user@example.com"));
    }

    @Test
    public void testValidEmail_withDotAndPlus() {
        assertTrue(validator.isValidEmail("user.name+tag@example.co.in"));
    }

    @Test
    public void testValidEmail_numericLocalAndDomain() {
        assertTrue(validator.isValidEmail("user123@domain123.com"));
    }

    @Test
    public void testValidEmail_withUnderscoreAndDash() {
        assertTrue(validator.isValidEmail("user_name-test@example.co"));
    }

    // Negative test cases
    @Test
    public void testInvalidEmail_missingAtSymbol() {
        assertFalse(validator.isValidEmail("userexample.com"));
    }

    @Test
    public void testInvalidEmail_multipleAtSymbols() {
        assertFalse(validator.isValidEmail("user@@example.com"));
    }

    @Test
    public void testInvalidEmail_missingDomain() {
        assertFalse(validator.isValidEmail("user@.com"));
    }

    @Test
    public void testInvalidEmail_invalidTLD() {
        assertFalse(validator.isValidEmail("user@example.toolongtld"));
    }

    @Test
    public void testInvalidEmail_emptyString() {
        assertFalse(validator.isValidEmail(""));
    }

    @Test
    public void testInvalidEmail_nullInput() {
        assertFalse(validator.isValidEmail(null));
    }
}
