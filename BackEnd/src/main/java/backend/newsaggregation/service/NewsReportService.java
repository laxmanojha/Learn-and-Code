package backend.newsaggregation.service;

import backend.newsaggregation.constants.StaticConfigurations;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.NewsReportDao;

public class NewsReportService {
	private NewsReportDao newsReportDao;
	private NewsDao newsDao;
    private static NewsReportService instance;
    private StaticConfigurations configurations;
	
	private NewsReportService() {
		this(NewsReportDao.getInstance(), NewsDao.getInstance(), StaticConfigurations.getInstance());
	}
	
	private NewsReportService(NewsReportDao newsReportDao, NewsDao newsDao, StaticConfigurations configurations) {
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
}
