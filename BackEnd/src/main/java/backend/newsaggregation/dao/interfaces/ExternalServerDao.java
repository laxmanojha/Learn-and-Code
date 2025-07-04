package backend.newsaggregation.dao.interfaces;

import java.util.List;
import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.dao.impl.ExternalServerDaoImpl;

public interface ExternalServerDao {
	
	static ExternalServerDao getInstance() {
        return ExternalServerDaoImpl.getInstance();
    }
	
    List<ExternalServer> getAllServersBasicDetails();
    List<ExternalServer> getAllServersWithApiKeys();
    ExternalServer getServerById(int id);
    boolean updateApiKey(int id, String newApiKey);
    boolean updateServerStatusAndLastAccess(int serverId, int statusId);
    ExternalServer getServerByName(String serverName);
}
