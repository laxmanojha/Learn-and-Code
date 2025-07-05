package backend.newsaggregation.service;

import java.util.List;
import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.model.HiddenKeyword;

public class AdminKeywordService {
	private static AdminKeywordService instance;
	private HiddenKeywordDao hiddenKeywordDao;
	
	private AdminKeywordService() {
		this(HiddenKeywordDao.getInstance());
	}
	
	private AdminKeywordService(HiddenKeywordDao hiddenKeywordDao) {
        this.hiddenKeywordDao = hiddenKeywordDao;
    }

    public static AdminKeywordService getInstance() {
        if (instance == null) {
            instance = new AdminKeywordService();
        }
        return instance;
    }
    
    public boolean addKeyword(String keyword) {
        return hiddenKeywordDao.addKeyword(keyword);
    }

    public boolean deleteKeyword(int id) {
        return hiddenKeywordDao.deleteKeyword(id);
    }

    public List<HiddenKeyword> getAllKeywords() {
        return hiddenKeywordDao.getAllKeywords();
    }
}

