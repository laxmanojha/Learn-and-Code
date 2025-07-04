package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.NewsReportDao;

public class NewsReportService {
	private NewsReportDao newsReportDao;
    private static NewsReportService instance;
	
	private NewsReportService() {
		this(NewsReportDao.getInstance());
	}
	
	private NewsReportService(NewsReportDao newsReportDao) {
        this.newsReportDao = newsReportDao;
    }

    public static NewsReportService getInstance() {
        if (instance == null) {
            instance = new NewsReportService();
        }
        return instance;
    }

    public boolean reportArticle(int userId, int newsId) {
        return newsReportDao.reportArticle(userId, newsId);
    }
}
