package backend.newsaggregation.ingestion;

import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.service.ExternalServerService;
import backend.newsaggregation.service.PersonalizedNewsService;
import backend.newsaggregation.service.UserService;
import backend.newsaggregation.util.EmailUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final UserService userService = UserService.getInstance();
    private final PersonalizedNewsService personalizedNewsService = PersonalizedNewsService.getInstance();

    private final List<ExternalNewsApi> apis = Arrays.asList(
            new NewsApi(),
            new TheNewsApi()
    );

    public void start() {
        Runnable fetchTask = () -> {
        	System.out.println("Scheduler is running at: " + new java.util.Date());
            ExternalServerService serverService = ExternalServerService.getInstance();
            List<NewsArticle> allNews = new ArrayList<>();

            for (ExternalNewsApi api : apis) {
                String apiName = api.getClass().getSimpleName();
                System.out.println("Fetching from " + apiName);

                try {
                    List<NewsArticle> fetchedNews = api.parseExternalApiData();
                    allNews.addAll(fetchedNews);

                    System.out.println("Fetched " + fetchedNews.size() + " articles from " + apiName);

                    serverService.updateServerStatus(apiName, true);

                } catch (Exception e) {
                    System.err.println("Error occurred while fetching from " + apiName + ": " + e.getMessage());
                    e.printStackTrace();

                    serverService.updateServerStatus(apiName, false);
                }

                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (!allNews.isEmpty()) {
                ExternalServerService.getInstance().saveDataFromApiToDB(allNews);
                Map<Integer, String> getAllUserIdsWithEmail = userService.getAllUserIdsWithEmail();
                for (Map.Entry<Integer, String> user : getAllUserIdsWithEmail.entrySet()) {
                    Integer userId = user.getKey();
                    String email = user.getValue();

                    if (email == null || email.isBlank()) continue;

                    List<NewsArticle> userPreferedArticles = personalizedNewsService.getPersonalizedArticles(userId, allNews);
                    if (userPreferedArticles == null || userPreferedArticles.isEmpty()) {
                        System.out.println("No personalized articles for: " + email);
                        continue;
                    }

                    try {
                        EmailUtil.sendNewsDigestEmail(email, userPreferedArticles);
                        System.out.println("Sent personalized news to: " + email);
                    } catch (Exception e) {
                        System.err.println("Failed to send email to: " + email);
                        e.printStackTrace();
                    }
                }

                System.out.println("Saved total " + allNews.size() + " articles to DB.");
            } else {
                System.out.println("No articles fetched in this cycle.");
            }
        };

        scheduler.scheduleAtFixedRate(fetchTask, 0, 3, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("Scheduler stopped.");
    }

    public static void main(String[] args) {
        NewsScheduler scheduler = new NewsScheduler();
        scheduler.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));
    }
}
