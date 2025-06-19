package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.UserDao;
import java.util.regex.Pattern;
import backend.newsaggregation.model.User;
import backend.newsaggregation.util.Util;

public class UserService {
	
	private static UserService instance;
	private UserDao userDao;
	private Util util;
	
	private UserService() {
		this(UserDao.getInstance(), Util.getInstance());
	}
	
	private UserService(UserDao userDao, Util util) {
        this.userDao = userDao;
        this.util = util;
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
	
	public String authenticateUser(User user) {
		String message = null;
		User userStoredData = getUserByUsername(user.getUsername());
		if (userStoredData == null)
			message = "0:User does not exists with username -> " + user.getUsername();
		else {			
			boolean passwordMatched = util.verifyPassword(user.getPassword(), userStoredData.getPassword());
			
			if (passwordMatched)
				message = "1:User Authentication Successful.";
			else
				message = "0:User Authentication Failed.";
		}
		
		return message;
	}

	public String registerUser(User user) {
	    String message;

	    if (!isValidInput(user)) {
	        message = "0:Invalid input. Username, email, and password must not be empty.";
	        return message;
	    }

	    System.out.println("User email: "+ user.getEmail());
	    if (!isValidEmail(user.getEmail())) {
	        message = "0:Invalid email format.";
	        return message;
	    }

	    User existingUser = getUserByUsername(user.getUsername());
	    if (existingUser != null) {
	        message = "0:Username already exists.";
	        return message;
	    }
	    
	    existingUser = getUserByEmail(user.getEmail());
	    if (existingUser != null) {
	    	message = "0:Email already exists.";
	    	return message;
	    }

	    String hashedPassword = util.hashPassword(user.getPassword()) ;
		user.setPassword(hashedPassword);
		boolean isInserted = userDao.saveUser(user);
	    message = isInserted ? "1:User registration successful."
	                         : "0:User registration failed.";
	    return message;
	}
	
	private boolean isValidInput(User user) {
	    if (user == null ||
	        user.getUsername() == null || user.getUsername().trim().isEmpty() ||
	        user.getEmail() == null || user.getEmail().trim().isEmpty() ||
	        user.getPassword() == null || user.getPassword().trim().isEmpty()) {

	        return false;
	    }
	    return true;
	}

	private boolean isValidEmail(String email) {
	    String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
	    boolean result = Pattern.matches(emailRegex, email);
	    System.out.println("Validating email: " + email + " → " + result);
	    return result;
	}
	
	public User getUserByUsername(String username) {
		User userInfo = userDao.getUserByUsername(username);
		if (userInfo != null)
			return userInfo;
		return null;
	}
	
	public User getUserByEmail(String email) {
		User userInfo = userDao.getUserByEmail(email);
		if (userInfo != null)
			return userInfo;
		return null;
	}
}
