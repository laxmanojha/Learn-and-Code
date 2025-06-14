package backend.newsaggregation.dao.interfaces;

import java.time.LocalDate;
import java.util.List;

import backend.newsaggregation.model.NewsArticle;

public interface SearchDao {
    List<NewsArticle> searchArticles(String keyword);
    List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate);
    List<NewsArticle> searchArticlesSorted(String keyword, String sortBy);
}
