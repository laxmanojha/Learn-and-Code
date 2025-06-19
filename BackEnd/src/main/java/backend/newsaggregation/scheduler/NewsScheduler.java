package backend.newsaggregation.scheduler;

import backend.newsaggregation.ingestion.ExternalNewsApi;
import backend.newsaggregation.ingestion.NewsApi;
import backend.newsaggregation.ingestion.TheNewsApi;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.ExternalServerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void start() {
        Runnable fetchTask = () -> {
            System.out.println("Fetching news at: " + new java.util.Date());
            List<NewsArticle> allNews = new ArrayList<>();

            try {
                // First API
                ExternalNewsApi newsApi = new NewsApi();
                List<NewsArticle> newsFromNewsApi = newsApi.parseExternalApiData();
                allNews.addAll(newsFromNewsApi);

                // Delay between two API calls
                Thread.sleep(30_000); // 30 seconds

                // Second API
                ExternalNewsApi theNewsApi = new TheNewsApi();
                List<NewsArticle> newsFromTheNewsApi = theNewsApi.parseExternalApiData();
                allNews.addAll(newsFromTheNewsApi);

                // Output or store
                for (NewsArticle news : allNews) {
                    System.out.println("🔹 Title: " + news.getTitle());
                    System.out.println("   ➤ Source: " + news.getSource());
                    System.out.println("   ➤ Categories: " + news.getCategories());
                    System.out.println("   ➤ Published At: " + news.getPublishedAt());
                    System.out.println("   ➤ URL: " + news.getUrl());
                    System.out.println("-------------------------------------------------");
                }

                ExternalServerService.getInstance().saveDataFromApiToDB(allNews);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Schedule the task every 3 hours
        scheduler.scheduleAtFixedRate(fetchTask, 0, 3, TimeUnit.MINUTES);
    }

    public void stop() {
        scheduler.shutdown();
    }

    public static void main(String[] args) {
        NewsScheduler scheduler = new NewsScheduler();
        scheduler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));
    }
}
