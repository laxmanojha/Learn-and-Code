package backend.newsaggregation.dao.impl;

import backend.newsaggregation.dao.interfaces.EmailConfigDao;
import backend.newsaggregation.model.EmailConfig;
import backend.newsaggregation.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailConfigDaoImpl implements EmailConfigDao {

	private static EmailConfigDaoImpl instance;
	Connection conn = DatabaseConfig.getConnection();
	private static final Logger logger = LoggerFactory.getLogger(EmailConfigDaoImpl.class);

    private EmailConfigDaoImpl() {}

    public static EmailConfigDaoImpl getInstance() {
        if (instance == null) {
            instance = new EmailConfigDaoImpl();
        }
        return instance;
    }

    @Override
    public EmailConfig getEmailConfig() {
        String sql = "SELECT * FROM email_config ORDER BY created_at DESC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                EmailConfig config = new EmailConfig();
                config.setId(rs.getInt("id"));
                config.setSenderEmail(rs.getString("sender_email"));
                config.setAppPassword(rs.getString("app_password"));
                config.setDisplayName(rs.getString("display_name"));
                return config;
            }

        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }

        return null;
    }
}

