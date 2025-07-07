package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.NewsReactionDao;
import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.dao.interfaces.NotificationKeywordPrefDao;
import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;
import backend.newsaggregation.model.NotificationPreference;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewsService {

    private static NewsService instance;
    private final NewsDao newsDao;
    private final HiddenKeywordDao hiddenKeywordDao;
    private final NotificationCategoryPrefDao notificationCategoryPrefDao;
    private final NotificationKeywordPrefDao notificationKeywordPrefDao;
    private final NewsReactionDao newsReactionDao;
    private final SavedArticleDao savedArticleDao;
    private final CategoryDao categoryDao;

    public NewsService() {
		this(NewsDao.getInstance(), HiddenKeywordDao.getInstance(), NotificationCategoryPrefDao.getInstance(), NotificationKeywordPrefDao.getInstance(), NewsReactionDao.getInstance(), SavedArticleDao.getInstance(), CategoryDao.getInstance());
	}
	
	public NewsService(NewsDao newsDao, HiddenKeywordDao hiddenKeywordDao, NotificationCategoryPrefDao notificationCategoryPrefDao, NotificationKeywordPrefDao notificationKeywordPrefDao, NewsReactionDao newsReactionDao, SavedArticleDao savedArticleDao, CategoryDao categoryDao) {
        this.newsDao = newsDao;
        this.hiddenKeywordDao = hiddenKeywordDao;
        this.notificationCategoryPrefDao = notificationCategoryPrefDao;
        this.notificationKeywordPrefDao = notificationKeywordPrefDao;
        this.newsReactionDao = newsReactionDao;
        this.savedArticleDao = savedArticleDao;
        this.categoryDao = categoryDao;
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
    
    public List<NewsArticle> getPersonalizedArticles(int userId, List<NewsArticle> articles) {
        Set<String> userKeywords = new HashSet<>();
        Set<Integer> likedNewsIds = new HashSet<>();
        Set<Integer> savedNewsIds = new HashSet<>();
        Set<String> preferredCategoryIds = new HashSet<>();

        // Step 1: Fetch user personalization data
        List<NotificationPreference> categoryPreference = notificationCategoryPrefDao.getCategoryPreferencesByUser(userId);
        for (NotificationPreference pref: categoryPreference) {
        	userKeywords.addAll(pref.getKeywords());
        }
        NotificationPreference keywordPreference = notificationKeywordPrefDao.getPreferencesByUser(userId);
        userKeywords.addAll(keywordPreference.getKeywords());
        likedNewsIds.addAll(newsReactionDao.getLikedNewsIds(userId));
        savedNewsIds.addAll(savedArticleDao.getSavedNewsIds(userId));
        preferredCategoryIds.addAll(categoryDao.getCategoryTypesForNews(likedNewsIds));
        preferredCategoryIds.addAll(categoryDao.getCategoryTypesForNews(savedNewsIds));

        // Step 2: Score articles
        List<ScoredArticle> scored = new ArrayList<>();
        for (NewsArticle article : articles) {
            int score = 0;

            // Keyword match
            for (String keyword : userKeywords) {
                if (containsIgnoreCase(article.getTitle(), keyword) ||
                    containsIgnoreCase(article.getDescription(), keyword) ||
                    containsIgnoreCase(article.getSnippet(), keyword)) {
                    score += 10;
                }
            }

            // Liked/Saved
            if (likedNewsIds.contains(article.getId())) score += 15;
            if (savedNewsIds.contains(article.getId())) score += 8;

            // Category match
            for (String category : article.getCategories()) {
                if (preferredCategoryIds.contains(category)) {
                    score += 5;
                    break; // matching one time only
                }
            }

            scored.add(new ScoredArticle(article, score));
        }

        // Step 3: Sort
        scored.sort((a, b) -> Integer.compare(b.score, a.score));

        return scored.stream().map(sa -> sa.article).toList();
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private static class ScoredArticle {
        NewsArticle article;
        int score;

        ScoredArticle(NewsArticle article, int score) {
            this.article = article;
            this.score = score;
        }
    }
}
