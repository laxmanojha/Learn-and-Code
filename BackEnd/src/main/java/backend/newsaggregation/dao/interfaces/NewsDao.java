package backend.newsaggregation.dao.interfaces;

import java.sql.Date;
import java.util.List;

import backend.newsaggregation.dao.impl.NewsDaoImpl;
import backend.newsaggregation.model.NewsArticle;

public interface NewsDao {
	
	static NewsDao getInstance() {
        return NewsDaoImpl.getInstance();
    }
	
    List<NewsArticle> getNewsByDate(Date date);
    List<NewsArticle> getNewsByDateAndCategory(Date date, String category);
    List<NewsArticle> getNewsByDateRange(Date startDate, Date endDate);
    List<NewsArticle> getNewsByDateRangeAndCategory(Date startDate, Date endDate, String category);
    NewsArticle getNewsById(int id);
}
