package backend.newsaggregation.model;

public class NewsCategory {
    private int id;
    private String categoryType;

    public NewsCategory() {}

    public NewsCategory(int id, String categoryType) {
        this.id = id;
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
