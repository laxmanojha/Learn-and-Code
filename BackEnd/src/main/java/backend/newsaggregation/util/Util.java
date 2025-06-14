package backend.newsaggregation.util;

import org.mindrot.jbcrypt.BCrypt;

public class Util {
	
	public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String enteredPassword, String storedHash) {
        return BCrypt.checkpw(enteredPassword, storedHash);
    }
}
