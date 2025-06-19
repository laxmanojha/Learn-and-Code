package aa;

public class ExternalNewsCategory {
    private int id;
    private String categoryType;

    public ExternalNewsCategory() {}
    public ExternalNewsCategory(String categoryType) {
        this.categoryType = categoryType;
    }
	public int getId() {
		return id;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

    
}