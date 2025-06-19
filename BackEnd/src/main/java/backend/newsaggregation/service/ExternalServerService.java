package backend.newsaggregation.service;

import java.util.List;

import backend.newsaggregation.dao.interfaces.ExternalServerDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.ingestion.TheNewsApi;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.util.Util;

public class ExternalServerService {
	private static ExternalServerService instance;
	private ExternalServerDao serverDao;
	private NewsDao newsDaoImpl;
	private Util util;
	
	private ExternalServerService() {
		this(ExternalServerDao.getInstance(), NewsDao.getInstance(), Util.getInstance());
	}
	
	private ExternalServerService(ExternalServerDao serverDao, NewsDao newsDaoImpl, Util util) {
        this.serverDao = serverDao;
        this.newsDaoImpl = newsDaoImpl;
        this.util = util;
    }

    public static ExternalServerService getInstance() {
        if (instance == null) {
            instance = new ExternalServerService();
        }
        return instance;
    }
    
    public List<ExternalServer> getAllServersBasicDetails() {
    	return serverDao.getAllServersBasicDetails();
    }

    public List<ExternalServer> getAllServersWithApiKeys() {
    	return serverDao.getAllServersWithApiKeys();
    }
    
    public ExternalServer getServerById(int id) {
    	return serverDao.getServerById(id);
    }
    
    public boolean updateApiKey(int id, String newApiKey) {
    	return serverDao.updateApiKey(id, newApiKey);
    }
    
    public void saveDataFromApiToDB(List<NewsArticle> apiData) {
    	for (NewsArticle newsData: apiData) {
    		newsDaoImpl.saveNews(newsData);
    		int newsId = newsDaoImpl.getLatestNewsArticleId();
    		for (NewsArticle data: apiData) {
    			for (String categoryType: data.getCategories()) {
    				int categoryId = newsDaoImpl.getOrInsertCategoryId(categoryType);
    				boolean mappingAdded = newsDaoImpl.insertNewsCategoryMapping(newsId, categoryId);
    				if (!mappingAdded)
    					System.out.println("Failed in adding newsID: " + newsId + " with categoryID: " + categoryId);
    			}
    		}    		
    	}
    }
}
