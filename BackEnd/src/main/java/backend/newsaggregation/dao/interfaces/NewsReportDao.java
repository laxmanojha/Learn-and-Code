package backend.newsaggregation.dao.interfaces;

import backend.newsaggregation.dao.impl.NewsReportDaoImpl;

public interface NewsReportDao {

	public static NewsReportDaoImpl getInstance() {
        return NewsReportDaoImpl.getInstance();
    }

	public boolean reportArticle(int userId, int newsId, String comment);
}
