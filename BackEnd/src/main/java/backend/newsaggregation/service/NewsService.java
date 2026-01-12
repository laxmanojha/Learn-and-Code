package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewsService {

    private static NewsService instance;
    private final NewsDao newsDao;
    private final HiddenKeywordDao hiddenKeywordDao;

    public NewsService() {
		this(NewsDao.getInstance(), HiddenKeywordDao.getInstance());
	}
	
	public NewsService(NewsDao newsDao, HiddenKeywordDao hiddenKeywordDao) {
        this.newsDao = newsDao;
        this.hiddenKeywordDao = hiddenKeywordDao;
    }

    public static NewsService getInstance() {
        if (instance == null) {
            instance = new NewsService();
        }
        return instance;
    }

    public List<NewsArticle> getTodayHeadlines() {
        Date today = Date.valueOf(LocalDate.now());
        List<NewsArticle> newsArticles = null;
        newsArticles = newsDao.getNewsByDate(today);
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
    
    private List<NewsArticle> filterUniqueById(List<NewsArticle> articles) {
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

    public List<NewsArticle> getHeadlinesByDateRange(Date start, Date end) {
    	List<NewsArticle> newsArticles = newsDao.getNewsByDateRange(start, end);
    	for (NewsArticle newsArticle: newsArticles) {
        	newsArticle = mapCategoriesToNews(newsArticle);
        }
    	newsArticles = filterUniqueById(newsArticles);
        return validNewsArticles(newsArticles);
    }
    
    public List<NewsArticle> getHeadlinesByDateRangeAndCategory(Date start, Date end, String category) {
    	List<NewsArticle> newsArticles = newsDao.getNewsByDateRangeAndCategory(start, end, category);
    	for (NewsArticle newsArticle: newsArticles) {
        	newsArticle = mapCategoriesToNews(newsArticle);
        }
        return validNewsArticles(newsArticles);
    }

    public NewsArticle getArticleById(int id) {
        NewsArticle newsArticles = newsDao.getNewsById(id);
    	return mapCategoriesToNews(newsArticles);
    }
}
