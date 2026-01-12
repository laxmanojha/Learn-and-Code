package backend.newsaggregation.service;

import java.util.List;

import backend.newsaggregation.constants.StaticConfigurations;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.NewsReportDao;
import backend.newsaggregation.model.NewsArticleReport;

public class NewsReportService {
	private NewsReportDao newsReportDao;
	private NewsDao newsDao;
    private static NewsReportService instance;
    private StaticConfigurations configurations;
	
	public NewsReportService() {
		this(NewsReportDao.getInstance(), NewsDao.getInstance(), StaticConfigurations.getInstance());
	}
	
	public NewsReportService(NewsReportDao newsReportDao, NewsDao newsDao, StaticConfigurations configurations) {
        this.newsReportDao = newsReportDao;
        this.newsDao = newsDao;
        this.configurations = configurations;
    }

    public static NewsReportService getInstance() {
        if (instance == null) {
            instance = new NewsReportService();
        }
        return instance;
    }

    public boolean reportArticle(int userId, int newsId, String comment) {
        boolean reported = newsReportDao.reportArticle(userId, newsId, comment);
        if (reportExceededThreshold(newsId)) {
        	hideArticle(newsId);
        }
        return reported;
    }
    
    public boolean reportExceededThreshold(int newsId) {
    	return newsDao.getReportCount(newsId) >= configurations.getReportThreshold();
    }
    
    public boolean hideArticle(int newsId) {
    	return newsDao.hideArticle(newsId);
    }
    
    public boolean unHideArticle(int newsId) {
    	return newsDao.unhideArticle(newsId);
    }
    
    public List<NewsArticleReport> getNewsArticleReport() {
    	return newsReportDao.getReportedArticles();
    }
}
