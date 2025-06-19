package aa;

import java.util.List;

interface ExternalApiSource {
    List<NewsArticle> fetchArticles() throws Exception;
    int getServerId();
}
