package com.itt.ecommerce.util;

import org.mindrot.jbcrypt.BCrypt;
import com.itt.ecommerce.dto.UserDto;

public class Util {
	public static UserDto getUserDto(String data) {
		return null;
	}
	
	public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String enteredPassword, String storedHash) {
        return BCrypt.checkpw(enteredPassword, storedHash);
    }
}
