package backend.newsaggregation.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartUpListener implements ServletContextListener {
    private NewsScheduler scheduler;
	private static final Logger logger = LoggerFactory.getLogger(AppStartUpListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = new NewsScheduler();
        scheduler.start();
        logger.info("[AppStartupListener] Scheduler started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.stop();
        }
        logger.info("[AppStartupListener] Scheduler stopped.");
    }
}
