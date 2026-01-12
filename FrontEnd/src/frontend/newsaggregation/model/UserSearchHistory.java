package frontend.newsaggregation.model;

import java.util.Date;

public class UserSearchHistory {
    private int id;
    private int userId;
    private String keyword;
    private Date searchedAt;

    public UserSearchHistory(int id, int userId, String keyword, Date searchedAt) {
        this.id = id;
        this.userId = userId;
        this.keyword = keyword;
        this.searchedAt = searchedAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Date getSearchedAt() {
        return searchedAt;
    }
}
