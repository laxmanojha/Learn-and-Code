package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NewsArticleCategoryInfo;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public class NewsService {

    private static NewsService instance;
    private final NewsDao newsDao;

    private NewsService() {
		this(NewsDao.getInstance());
	}
	
	private NewsService(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    public static NewsService getInstance() {
        if (instance == null) {
            instance = new NewsService();
        }
        return instance;
    }

    public List<NewsArticle> getTodayHeadlines() {
        Date today = Date.valueOf(LocalDate.now());
        List<NewsArticle> newsArticles = newsDao.getNewsByDate(today);
        return mapCategoriesToNews(newsArticles);
    }
    
    private List<NewsArticle> mapCategoriesToNews(List<NewsArticle> newsArticles) {
    	List<NewsArticleCategoryInfo> articleCategoryInfos = newsDao.getAllCategory();
    	for (NewsArticle newsArticle: newsArticles) {
    		for (NewsArticleCategoryInfo articleCategoryInfo: articleCategoryInfos) {
    			if (newsArticle.getId() == articleCategoryInfo.getNewsId()) {
    				newsArticle.getCategories().add(articleCategoryInfo.getCategoryType());
    			}
    		}
    	}
    	
    	return newsArticles;
    }
    
    private NewsArticle mapCategoriesToNews(NewsArticle newsArticle) {
    	List<NewsArticleCategoryInfo> articleCategoryInfos = newsDao.getAllCategory();
		for (NewsArticleCategoryInfo articleCategoryInfo: articleCategoryInfos) {
			if (newsArticle.getId() == articleCategoryInfo.getNewsId()) {
				newsArticle.getCategories().add(articleCategoryInfo.getCategoryType());
			}
		}
    	
    	return newsArticle;
    }

    public List<NewsArticle> getHeadlinesByDateRange(Date start, Date end) {
    	List<NewsArticle> newsArticles = newsDao.getNewsByDateRange(start, end);
    	return mapCategoriesToNews(newsArticles);
    }
    
    public List<NewsArticle> getHeadlinesByDateRangeAndCategory(Date start, Date end, String category) {
    	List<NewsArticle> newsArticles = newsDao.getNewsByDateRangeAndCategory(start, end, category);
    	return mapCategoriesToNews(newsArticles);
    }

    public NewsArticle getArticleById(int id) {
        NewsArticle newsArticles = newsDao.getNewsById(id);
    	return mapCategoriesToNews(newsArticles);
    }

//    private Date truncateToDate(Date dateTime) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(dateTime);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE,      0);
//        calendar.set(Calendar.SECOND,      0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        return calendar.getTime();
//    }
}
