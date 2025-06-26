package frontend.newsaggregation.model;

import java.sql.Date;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private int roleId;
    private String roleType;
    private Date notificationViewedAt;
    
    public User() {}
    
    public User(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    public User(String username, String email, String password) {
    	this.username = username;
    	this.email = email;
    	this.password = password;
    }
    
    public User(String username, String password, String email, int roleId) {
    	this.username = username;
    	this.password = password;
    	this.email = email;
    	this.roleId = roleId;
    }
    
    public User(int id, String username, String password, String email, int roleId, String roleType, Date notificationViewedAt) {
    	this.id = id;
    	this.username = username;
    	this.password = password;
    	this.email = email;
    	this.roleId = roleId;
    	this.roleType = roleType;
    	this.notificationViewedAt = notificationViewedAt;
    }

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public int getRoleId() {
		return roleId;
	}
	
	public String getRoleType() {
		return roleType;
	}

	public Date getNotificationViewedAt() {
		return notificationViewedAt;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	
	public void setNotificationViewedAt(Date notificationViewedAt) {
		this.notificationViewedAt = notificationViewedAt;
	}
}
