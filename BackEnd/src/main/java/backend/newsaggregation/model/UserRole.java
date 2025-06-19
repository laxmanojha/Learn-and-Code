package backend.newsaggregation.model;

public class UserRole {
    private int id;
    private String roleName;

    public UserRole() {}

    public UserRole(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

	public int getId() {
		return id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}
