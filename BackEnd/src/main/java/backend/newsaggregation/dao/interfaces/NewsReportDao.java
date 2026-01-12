package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.NewsReportDaoImpl;
import backend.newsaggregation.model.NewsArticleReport;

public interface NewsReportDao {

	public static NewsReportDaoImpl getInstance() {
        return NewsReportDaoImpl.getInstance();
    }

	public boolean reportArticle(int userId, int newsId, String comment);
	public List<NewsArticleReport> getReportedArticles();
}
