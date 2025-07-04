package backend.newsaggregation.ingestion;

import java.util.List;

import backend.newsaggregation.model.NewsArticle;

public interface ExternalNewsApi {
	
	List<NewsArticle> parseExternalApiData();
}
