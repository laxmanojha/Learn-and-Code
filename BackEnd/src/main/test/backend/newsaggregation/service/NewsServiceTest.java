package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.*;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import backend.newsaggregation.model.NotificationPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsServiceTest {

    private NewsDao newsDao;
    private HiddenKeywordDao hiddenKeywordDao;
    private NotificationCategoryPrefDao categoryPrefDao;
    private NotificationKeywordPrefDao keywordPrefDao;
    private NewsReactionDao reactionDao;
    private SavedArticleDao savedArticleDao;
    private CategoryDao categoryDao;
    private NewsService newsService;

    @BeforeEach
    public void setup() {
        newsDao = mock(NewsDao.class);
        hiddenKeywordDao = mock(HiddenKeywordDao.class);
        categoryPrefDao = mock(NotificationCategoryPrefDao.class);
        keywordPrefDao = mock(NotificationKeywordPrefDao.class);
        reactionDao = mock(NewsReactionDao.class);
        savedArticleDao = mock(SavedArticleDao.class);
        categoryDao = mock(CategoryDao.class);
        newsService = new NewsService(newsDao, hiddenKeywordDao, categoryPrefDao, keywordPrefDao, reactionDao, savedArticleDao, categoryDao);
    }

    @Test
    public void testGetTodayHeadlines_validArticlesFilteredByKeyword() {
        NewsArticle article1 = createArticle(1, "Safe Title");
        NewsArticle article2 = createArticle(2, "Blocked Word");

        when(newsDao.getNewsByDate(any())).thenReturn(List.of(article1, article2));
        when(newsDao.getAllCategory(anyInt())).thenReturn(List.of());
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(List.of(createKeyword("Blocked")));

        List<NewsArticle> result = newsService.getTodayHeadlines();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    public void testGetHeadlinesByDateRange_shouldReturnValidArticles() {
        NewsArticle article = createArticle(1, "News");
        when(newsDao.getNewsByDateRange(any(), any())).thenReturn(List.of(article));
        when(newsDao.getAllCategory(anyInt())).thenReturn(List.of());
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(List.of());

        List<NewsArticle> result = newsService.getHeadlinesByDateRange(Date.valueOf("2025-01-01"), Date.valueOf("2025-01-10"));
        assertEquals(1, result.size());
    }

    @Test
    public void testGetHeadlinesByDateRangeAndCategory_shouldReturnFiltered() {
        NewsArticle article = createArticle(1, "Title");
        when(newsDao.getNewsByDateRangeAndCategory(any(), any(), anyString())).thenReturn(List.of(article));
        when(newsDao.getAllCategory(anyInt())).thenReturn(List.of());
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(List.of());

        List<NewsArticle> result = newsService.getHeadlinesByDateRangeAndCategory(Date.valueOf("2025-01-01"), Date.valueOf("2025-01-10"), "sports");
        assertEquals(1, result.size());
    }

    @Test
    public void testGetArticleById_shouldReturnMapped() {
        NewsArticle article = createArticle(1, "Any");
        when(newsDao.getNewsById(1)).thenReturn(article);
        when(newsDao.getAllCategory(1)).thenReturn(List.of());

        NewsArticle result = newsService.getArticleById(1);
        assertEquals(1, result.getId());
    }

    @Test
    public void testGetPersonalizedArticles_shouldScoreAndSortArticles() {
        NewsArticle article = createArticle(1, "AI and ML");
        article.setCategories(List.of("tech"));

        NotificationPreference catPref = new NotificationPreference();
        catPref.setKeywords(List.of("AI"));
        NotificationPreference keyPref = new NotificationPreference();
        keyPref.setKeywords(List.of("ML"));

        when(categoryPrefDao.getCategoryPreferencesByUser(1)).thenReturn(List.of(catPref));
        when(keywordPrefDao.getPreferencesByUser(1)).thenReturn(keyPref);
        when(reactionDao.getLikedNewsIds(1)).thenReturn(List.of(1));
        when(savedArticleDao.getSavedNewsIds(1)).thenReturn(List.of());
        when(categoryDao.getCategoryTypesForNews(anySet())).thenReturn(Set.of("tech"));

        List<NewsArticle> result = newsService.getPersonalizedArticles(1, List.of(article));
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    private NewsArticle createArticle(int id, String title) {
        NewsArticle article = new NewsArticle();
        article.setId(id);
        article.setTitle(title);
        article.setDescription("");
        article.setSnippet("");
        article.setCategories(new ArrayList<>());
        return article;
    }

    private HiddenKeyword createKeyword(String keyword) {
        HiddenKeyword k = new HiddenKeyword();
        k.setKeyword(keyword);
        return k;
    }
}
