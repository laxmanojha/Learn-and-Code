package backend.newsaggregation.service;

import java.time.LocalDate;
import java.util.List;

import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.model.NewsArticle;

public class SearchNewsService {
	
	private static SearchNewsService instance;
    private final SearchDao searchDao;

    private SearchNewsService() {
		this(SearchDao.getInstance());
	}
	
	private SearchNewsService(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    public static SearchNewsService getInstance() {
        if (instance == null) {
            instance = new SearchNewsService();
        }
        return instance;
    }

	public List<NewsArticle> searchArticles(String query, String startDateStr, String endDateStr, String sort) {
        LocalDate start = null;
        LocalDate end = null;

        if (startDateStr != null && endDateStr != null) {
            start = LocalDate.parse(startDateStr);
            end = LocalDate.parse(endDateStr);
        }

        boolean hasDateRange = (start != null && end != null);
        boolean hasSort = (sort != null && (sort.equalsIgnoreCase("likes") || sort.equalsIgnoreCase("dislikes")));

        if (hasDateRange && hasSort) {
            return searchDao.searchArticles(query, start, end);
        } else if (hasDateRange) {
            return searchDao.searchArticles(query, start, end);
        } else if (hasSort) {
            return searchDao.searchArticlesSorted(query, sort);
        } else {
            return searchDao.searchArticles(query);
        }
    }
}
