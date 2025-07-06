package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.dao.interfaces.NewsReactionDao;
import backend.newsaggregation.dao.interfaces.NotificationCategoryPrefDao;
import backend.newsaggregation.dao.interfaces.NotificationKeywordPrefDao;
import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NotificationPreference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonalizedNewsService {

    private static PersonalizedNewsService instance;
    private final NotificationCategoryPrefDao notificationCategoryPrefDao;
    private final NotificationKeywordPrefDao notificationKeywordPrefDao;
    private final NewsReactionDao newsReactionDao;
    private final SavedArticleDao savedArticleDao;
    private final CategoryDao categoryDao;

    private PersonalizedNewsService() {
		this(NotificationCategoryPrefDao.getInstance(), NotificationKeywordPrefDao.getInstance(), NewsReactionDao.getInstance(), SavedArticleDao.getInstance(), CategoryDao.getInstance());
	}
	
	private PersonalizedNewsService(NotificationCategoryPrefDao notificationCategoryPrefDao, NotificationKeywordPrefDao notificationKeywordPrefDao, NewsReactionDao newsReactionDao, SavedArticleDao savedArticleDao, CategoryDao categoryDao) {
        this.notificationCategoryPrefDao = notificationCategoryPrefDao;
        this.notificationKeywordPrefDao = notificationKeywordPrefDao;
        this.newsReactionDao = newsReactionDao;
        this.savedArticleDao = savedArticleDao;
        this.categoryDao = categoryDao;
    }

    public static PersonalizedNewsService getInstance() {
        if (instance == null) {
            instance = new PersonalizedNewsService();
        }
        return instance;
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

        // Step 3: Filter out zero-score articles and sort
        List<NewsArticle> personalizedArticles = scored.stream()
            .filter(sa -> sa.score > 0)
            .sorted((a, b) -> Integer.compare(b.score, a.score))
            .map(sa -> sa.article)
            .toList();

        return personalizedArticles;
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
