package backend.newsaggregation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.dao.interfaces.UserSearchHistoryDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;

public class SearchNewsService {
	
	private static SearchNewsService instance;
    private final SearchDao searchDao;
    private final NewsDao newsDao;
    private final HiddenKeywordDao hiddenKeywordDao;
    private final UserSearchHistoryDao userSearchHistoryDao;

    private SearchNewsService() {
		this(SearchDao.getInstance(), NewsDao.getInstance(), HiddenKeywordDao.getInstance(), UserSearchHistoryDao.getInstance());
	}
	
	private SearchNewsService(SearchDao searchDao, NewsDao newsDao, HiddenKeywordDao hiddenKeywordDao, UserSearchHistoryDao userSearchHistoryDao) {
        this.searchDao = searchDao;
        this.newsDao = newsDao;
        this.hiddenKeywordDao = hiddenKeywordDao;
        this.userSearchHistoryDao = userSearchHistoryDao;
    }

    public static SearchNewsService getInstance() {
        if (instance == null) {
            instance = new SearchNewsService();
        }
        return instance;
    }

	public List<NewsArticle> searchArticles(int userId, String query, String startDateStr, String endDateStr, String sort) {
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
        
        userSearchHistoryDao.logSearch(userId, query);
        
        for (NewsArticle newsArticle: newsArticles) {
        	newsArticle = mapCategoriesToNews(newsArticle);
        }
        newsArticles = filterUniqueById(newsArticles);
        return validNewsArticles(newsArticles);
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
    

    private List<NewsArticle> validNewsArticles(List<NewsArticle> articles) {
    	List<HiddenKeyword> blockedKeywords = hiddenKeywordDao.getAllKeywords();
    	List<NewsArticle> filteredList = articles.stream()
    	    .filter(article -> !containsBlockedKeyword(article, blockedKeywords))
    	    .toList();
    	
    	return filteredList;
    }
    
    private boolean containsBlockedKeyword(NewsArticle article, List<HiddenKeyword> blockedKeywords) {
    	String title = article.getTitle();
    	String description = article.getDescription() == null ? "" : article.getDescription();
    	String snippet = article.getSnippet() == null ? "" : article.getSnippet();
        String content = (title + " " + description + " " + snippet).toLowerCase();
        return blockedKeywords.stream().anyMatch(kw -> content.contains(kw.getKeyword().toLowerCase()));
    }
    
    public List<String> getRecentSearchKeywords(int userId) {
    	int KEYWORD_LIMIT = 20;
    	return userSearchHistoryDao.getRecentSearchKeywords(userId, KEYWORD_LIMIT);
    }
}
