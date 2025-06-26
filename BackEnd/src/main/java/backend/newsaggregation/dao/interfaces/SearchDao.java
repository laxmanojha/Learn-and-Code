package backend.newsaggregation.dao.interfaces;

import java.time.LocalDate;
import java.util.List;

import backend.newsaggregation.dao.impl.SearchDaoImpl;
import backend.newsaggregation.model.NewsArticle;

public interface SearchDao {
	
	static SearchDao getInstance() {
        return SearchDaoImpl.getInstance();
    }
	
    List<NewsArticle> searchArticles(String keyword);
    List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate);
    List<NewsArticle> searchArticlesSorted(String keyword, String sortBy);
    List<NewsArticle> searchArticles(String keyword, LocalDate startDate, LocalDate endDate, String sortBy);

}
