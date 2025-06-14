package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.model.ExternalServer;

public interface ExternalServerDao {
    List<ExternalServer> getAllServersWithStatus();
    List<ExternalServer> getAllServersWithDetails();
    ExternalServer getServerById(int id);
    boolean updateApiKey(int id, String newApiKey);
}
