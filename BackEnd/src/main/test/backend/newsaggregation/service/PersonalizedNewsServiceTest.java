package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.*;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NotificationPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PersonalizedNewsServiceTest {

    private NotificationCategoryPrefDao categoryPrefDaoMock;
    private NotificationKeywordPrefDao keywordPrefDaoMock;
    private NewsReactionDao newsReactionDaoMock;
    private SavedArticleDao savedArticleDaoMock;
    private CategoryDao categoryDaoMock;
    private SearchNewsService searchNewsServiceMock;
    private PersonalizedNewsService personalizedNewsService;

    @BeforeEach
    public void setup() {
        categoryPrefDaoMock = mock(NotificationCategoryPrefDao.class);
        keywordPrefDaoMock = mock(NotificationKeywordPrefDao.class);
        newsReactionDaoMock = mock(NewsReactionDao.class);
        savedArticleDaoMock = mock(SavedArticleDao.class);
        categoryDaoMock = mock(CategoryDao.class);
        searchNewsServiceMock = mock(SearchNewsService.class);
        personalizedNewsService = new PersonalizedNewsService(categoryPrefDaoMock, keywordPrefDaoMock, newsReactionDaoMock, savedArticleDaoMock, categoryDaoMock, searchNewsServiceMock);
    }

    @Test
    public void testGetPersonalizedArticles_withScoring_shouldReturnSortedList() {
        int userId = 1;

        NewsArticle article1 = createArticle(101, "AI News", "Advancements in AI", "Deep Learning", List.of("technology"));
        NewsArticle article2 = createArticle(102, "Politics", "Elections", "2024 Vote", List.of("politics"));
        List<NewsArticle> articles = List.of(article1, article2);

        NotificationPreference pref1 = new NotificationPreference();
        pref1.setKeywords(List.of("AI", "Machine Learning"));
        NotificationPreference keywordPref = new NotificationPreference();
        keywordPref.setKeywords(List.of("Deep Learning"));

        when(categoryPrefDaoMock.getCategoryPreferencesByUser(userId)).thenReturn(List.of(pref1));
        when(keywordPrefDaoMock.getPreferencesByUser(userId)).thenReturn(keywordPref);
        when(searchNewsServiceMock.getRecentSearchKeywords(userId)).thenReturn(List.of("Neural Networks"));
        when(newsReactionDaoMock.getLikedNewsIds(userId)).thenReturn(List.of(101));
        when(savedArticleDaoMock.getSavedNewsIds(userId)).thenReturn(List.of(102));
        when(categoryDaoMock.getCategoryTypesForNews(Set.of(101))).thenReturn(Set.of("technology"));
        when(categoryDaoMock.getCategoryTypesForNews(Set.of(102))).thenReturn(Set.of("politics"));

        List<NewsArticle> result = personalizedNewsService.getPersonalizedArticles(userId, articles);

        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getId());
        assertEquals(102, result.get(1).getId());
    }

    @Test
    public void testGetPersonalizedArticles_zeroScore_shouldFilterOut() {
        int userId = 2;

        NewsArticle article = createArticle(201, "Unrelated", "Nothing relevant", "Off topic", List.of("sports"));
        List<NewsArticle> articles = List.of(article);

        NotificationPreference pref = new NotificationPreference();
        pref.setKeywords(List.of("AI"));
        NotificationPreference keywordPref = new NotificationPreference();
        keywordPref.setKeywords(List.of());

        when(categoryPrefDaoMock.getCategoryPreferencesByUser(userId)).thenReturn(List.of(pref));
        when(keywordPrefDaoMock.getPreferencesByUser(userId)).thenReturn(keywordPref);
        when(searchNewsServiceMock.getRecentSearchKeywords(userId)).thenReturn(List.of());
        when(newsReactionDaoMock.getLikedNewsIds(userId)).thenReturn(List.of());
        when(savedArticleDaoMock.getSavedNewsIds(userId)).thenReturn(List.of());
        when(categoryDaoMock.getCategoryTypesForNews(anySet())).thenReturn(Set.of());

        List<NewsArticle> result = personalizedNewsService.getPersonalizedArticles(userId, articles);
        assertTrue(result.isEmpty());
    }

    private NewsArticle createArticle(int id, String title, String desc, String snippet, List<String> categories) {
        NewsArticle article = new NewsArticle();
        article.setId(id);
        article.setTitle(title);
        article.setDescription(desc);
        article.setSnippet(snippet);
        article.setCategories(new ArrayList<>(categories));
        return article;
    }
}
