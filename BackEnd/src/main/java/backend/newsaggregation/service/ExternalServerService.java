package backend.newsaggregation.service;

import java.util.List;

import backend.newsaggregation.dao.interfaces.ExternalServerDao;
import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.util.Util;

public class ExternalServerService {
	private static ExternalServerService instance;
	private ExternalServerDao serverDao;
	private Util util;
	
	private ExternalServerService() {
		this(ExternalServerDao.getInstance(), Util.getInstance());
	}
	
	private ExternalServerService(ExternalServerDao serverDao, Util util) {
        this.serverDao = serverDao;
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
}
