package backend.newsaggregation.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {

    private final Util util = Util.getInstance();

    @Test
    public void testHashPassword_NotNullOrEmpty() {
        String password = "securePassword123!";
        String hashed = util.hashPassword(password);

        assertNotNull(hashed);
        assertFalse(hashed.isEmpty());
        assertNotEquals(password, hashed); // Make sure it's hashed, not plain
    }

    @Test
    public void testVerifyPassword_CorrectPassword() {
        String password = "mySecret";
        String hashed = util.hashPassword(password);

        assertTrue(util.verifyPassword(password, hashed));
    }

    @Test
    public void testVerifyPassword_WrongPassword() {
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hashed = util.hashPassword(correctPassword);

        assertFalse(util.verifyPassword(wrongPassword, hashed));
    }

    @Test
    public void testVerifyPassword_NullPassword() {
        String hashed = util.hashPassword("abc123");

        assertDoesNotThrow(() -> {
            util.verifyPassword(null, hashed);
        });
    }

    @Test
    public void testVerifyPassword_NullHash() {
        assertThrows(java.lang.NullPointerException.class, () -> {
            util.verifyPassword("abc123", null);
        });
    }

    @Test
    public void testVerifyPassword_InvalidHashFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            util.verifyPassword("abc123", "not-a-valid-hash");
        });
    }
}
