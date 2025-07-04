package frontend.newsaggregation.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsArticle {
	private int id;
    private String title;
    private String description;
    private String snippet;
    private Date publishedAt;
    private String url;
    private String imageUrl;
    private String source;
    private List<String> categories;
	
	public NewsArticle() {}
	
	public NewsArticle(int id, String title, String description, String source, String url, int categoryId, Date publishedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.source = source;
        this.url = url;
        this.publishedAt = publishedAt;
        this.categories = new ArrayList<>();
    }

	public NewsArticle(String title, String description, String snippet, Date publishedAt, String url,
			String imageUrl, String source, List<String> categories) {
		super();
		this.title = title;
		this.description = description;
		this.snippet = snippet;
		this.publishedAt = publishedAt;
		this.url = url;
		this.imageUrl = imageUrl;
		this.source = source;
		this.categories = categories;
	}

	public NewsArticle(int id, String title, String description, String snippet, Date publishedAt, String url,
			String imageUrl, String source, List<String> categories) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.snippet = snippet;
		this.publishedAt = publishedAt;
		this.url = url;
		this.imageUrl = imageUrl;
		this.source = source;
		this.categories = categories;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getSnippet() {
		return snippet;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public String getUrl() {
		return url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getSource() {
		return source;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	@Override
	public String toString() {
	    return "Article ID: " + id + "\n"
	            + title + "\n"
	            + (snippet != null ? snippet : description) + "\n"
	            + "Source: " + source + "\n"
	            + "URL: " + url + "\n"
	            + "Categories: " + (categories != null && !categories.isEmpty() ? String.join(", ", categories) : "General") + "\n"
	            + "Published At: " + publishedAt;
	}
}
