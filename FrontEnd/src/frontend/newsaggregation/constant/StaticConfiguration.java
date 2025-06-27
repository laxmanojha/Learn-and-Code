package frontend.newsaggregation.constant;

public class StaticConfiguration {
    private static final String BASE_URL = "http://localhost:8080/backend/api/";
    
    public static String getBaseUrl() {
    	return BASE_URL;
    }
}
