package backend.newsaggregation.ingestion;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartUpListener implements ServletContextListener {
    private NewsScheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = new NewsScheduler();
        scheduler.start();
        System.out.println("[AppStartupListener] Scheduler started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.stop();
        }
        System.out.println("[AppStartupListener] Scheduler stopped.");
    }
}
