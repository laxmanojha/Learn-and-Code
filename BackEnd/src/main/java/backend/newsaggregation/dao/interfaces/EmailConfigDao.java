package backend.newsaggregation.dao.interfaces;


import backend.newsaggregation.dao.impl.EmailConfigDaoImpl;
import backend.newsaggregation.model.EmailConfig;

public interface EmailConfigDao {
	
	static EmailConfigDao getInstance() {
		return EmailConfigDaoImpl.getInstance();
	}
	
    public EmailConfig getEmailConfig();
}
