package backend.newsaggregation.constants;

public class StaticConfigurations {

	private final String APPLICATION_PROP_PATH = "/backend/newsaggregation/resource/Application.properties";
	private final int REPORT_THRESHOLD = 5;
	private static StaticConfigurations instance;
	
	private StaticConfigurations() {}

    public static StaticConfigurations getInstance() {
        if (instance == null) {
            instance = new StaticConfigurations();
        }
        return instance;
    }
	
	public String getPath() {
		return APPLICATION_PROP_PATH;
	}
	
	public int getReportThreshold() {
		return REPORT_THRESHOLD;
	}
}
