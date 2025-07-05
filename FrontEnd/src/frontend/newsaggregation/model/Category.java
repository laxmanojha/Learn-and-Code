package frontend.newsaggregation.model;

public class Category {
	private int id;
    private String name;
    private boolean isHidden;
    
    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Category(int id, String name, boolean isHidden) {
    	this.id = id;
    	this.name = name;
    	this.isHidden = isHidden;
    }

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
