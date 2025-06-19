package aa;

import java.util.Date;
import java.util.List;

public class NewsArticle {
    private int id;
    private String uuid;
    private String title;
    private String description;
    private String snippet;
    private String language;
    private Date publishedAt;
    private Date createdAt;
    private String locale;
    private String url;
    private String imageUrl;
    private String source;
    private Float relevanceScore;
    private List<String> categories;
	public int getId() {
		return id;
	}
	public String getUuid() {
		return uuid;
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
	public String getLanguage() {
		return language;
	}
	public Date getPublishedAt() {
		return publishedAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public String getLocale() {
		return locale;
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
	public Float getRelevanceScore() {
		return relevanceScore;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public void setLanguage(String language) {
		this.language = language;
	}
	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public void setLocale(String locale) {
		this.locale = locale;
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
	public void setRelevanceScore(Float relevanceScore) {
		this.relevanceScore = relevanceScore;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

}
