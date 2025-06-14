package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.model.NewsArticle;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsService {

    private static NewsService instance;
    private final NewsDao newsDao;
    private final SearchDao searchDao;

    private NewsService() {
		this(NewsDao.getInstance(), SearchDao.getInstance());
	}
	
	private NewsService(NewsDao newsDao, SearchDao searchDao) {
        this.newsDao = newsDao;
        this.searchDao = searchDao;
    }

    public static NewsService getInstance() {
        if (instance == null) {
            instance = new NewsService();
        }
        return instance;
    }

    public List<NewsArticle> getTodayHeadlines() {
        Date today = truncateToDate(new Date());
        return newsDao.getNewsByDate(today);
    }

    public List<NewsArticle> getHeadlinesByDateRange(Date start, Date end) {
    	return newsDao.getNewsByDateRange(start, end);
    }
    
    public List<NewsArticle> getHeadlinesByDateRangeAndCategory(Date start, Date end, String category) {
    	return newsDao.getNewsByDateRangeAndCategory(start, end, category);
    }

    public NewsArticle getArticleById(int id) {
        return newsDao.getNewsById(id);
    }
    
    public List<NewsArticle> searchArticles(String query, String startDateStr, String endDateStr, String sort) {
        LocalDate start = null;
        LocalDate end = null;

        if (startDateStr != null && endDateStr != null) {
            start = LocalDate.parse(startDateStr);
            end = LocalDate.parse(endDateStr);
        }

        boolean hasDateRange = (start != null && end != null);
        boolean hasSort = (sort != null && (sort.equalsIgnoreCase("likes") || sort.equalsIgnoreCase("dislikes")));

        if (hasDateRange && hasSort) {
            return searchDao.searchArticles(query, start, end);
        } else if (hasDateRange) {
            return searchDao.searchArticles(query, start, end);
        } else if (hasSort) {
            return searchDao.searchArticlesSorted(query, sort);
        } else {
            return searchDao.searchArticles(query);
        }
    }

    private Date truncateToDate(Date dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,      0);
        calendar.set(Calendar.SECOND,      0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}

