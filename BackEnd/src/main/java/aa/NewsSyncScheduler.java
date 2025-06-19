package aa;


import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NewsSyncScheduler {
    public static void startScheduler() {
        NewsIngestionService service = new NewsIngestionService();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            service::syncAllSources,
            0, 3, TimeUnit.HOURS
        );
    }
}
