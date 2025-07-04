package backend.newsaggregation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;

public class SearchNewsService {
	
	private static SearchNewsService instance;
    private final SearchDao searchDao;
    private final NewsDao newsDao;

    private SearchNewsService() {
		this(SearchDao.getInstance(), NewsDao.getInstance());
	}
	
	private SearchNewsService(SearchDao searchDao, NewsDao newsDao) {
        this.searchDao = searchDao;
        this.newsDao = newsDao;
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
        List<NewsArticle> newsArticles = null;

        if (startDateStr != null && endDateStr != null) {
            start = LocalDate.parse(startDateStr);
            end = LocalDate.parse(endDateStr);
        }
        boolean hasDateRange = (start != null && end != null);
        boolean hasSort = (sort != null && (sort.equalsIgnoreCase("likes") || sort.equalsIgnoreCase("dislikes")));
        if (hasDateRange && hasSort) {
        	newsArticles = searchDao.searchArticles(query, start, end, sort);
        } else if (hasDateRange) {
        	newsArticles = searchDao.searchArticles(query, start, end);
        } else if (hasSort) {
        	newsArticles = searchDao.searchArticlesSorted(query, sort);
        } else {
        	newsArticles = searchDao.searchArticles(query);
        }
        
        for (NewsArticle newsArticle: newsArticles) {
        	newsArticle = mapCategoriesToNews(newsArticle);
        }
        return filterUniqueById(newsArticles);
        
    }
	

    private NewsArticle mapCategoriesToNews(NewsArticle newsArticle) {
    	List<NewsArticleCategoryInfo> articleCategoryInfos = newsDao.getAllCategory(newsArticle.getId());
		for (NewsArticleCategoryInfo articleCategoryInfo: articleCategoryInfos) {
			if (newsArticle.getId() == articleCategoryInfo.getNewsId()) {
				newsArticle.getCategories().add(articleCategoryInfo.getCategoryType());
			}
		}
    	
    	return newsArticle;
    }
    
    private static List<NewsArticle> filterUniqueById(List<NewsArticle> articles) {
        Map<Integer, NewsArticle> uniqueMap = new LinkedHashMap<>();
        for (NewsArticle article : articles) {
            uniqueMap.putIfAbsent(article.getId(), article);
        }
        return new ArrayList<>(uniqueMap.values());
    }
}
