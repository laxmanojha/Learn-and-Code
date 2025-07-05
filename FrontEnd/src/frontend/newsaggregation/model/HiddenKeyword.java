package frontend.newsaggregation.model;

import java.util.Date;

public class HiddenKeyword {
    private int id;
    private String keyword;
    private Date createdAt;

    public int getId() { return id; }
    public String getKeyword() { return keyword; }
    public Date getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "ID: " + id + " | Keyword: " + keyword + " | Created At: " + createdAt;
    }
}
