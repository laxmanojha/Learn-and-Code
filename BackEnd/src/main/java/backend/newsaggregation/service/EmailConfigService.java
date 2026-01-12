package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.EmailConfigDao;
import backend.newsaggregation.model.EmailConfig;

public class EmailConfigService {
    private static EmailConfigService instance;
    private final EmailConfigDao emailConfigDao;

    public EmailConfigService() {
        this.emailConfigDao = EmailConfigDao.getInstance();
    }
    
    public EmailConfigService(EmailConfigDao emailConfigDao) {
        this.emailConfigDao = emailConfigDao;
    }

    public static EmailConfigService getInstance() {
        if (instance == null) {
            instance = new EmailConfigService();
        }
        return instance;
    }

    public EmailConfig getEmailConfig() {
        return emailConfigDao.getEmailConfig();
    }
}
