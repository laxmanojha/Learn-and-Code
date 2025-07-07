package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SearchDao;
import backend.newsaggregation.dao.interfaces.UserSearchHistoryDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SearchNewsServiceTest {

    private SearchDao searchDaoMock;
    private NewsDao newsDaoMock;
    private HiddenKeywordDao hiddenKeywordDaoMock;
    private UserSearchHistoryDao userSearchHistoryDaoMock;
    private SearchNewsService searchNewsService;

    @BeforeEach
    public void setup() {
        searchDaoMock = mock(SearchDao.class);
        newsDaoMock = mock(NewsDao.class);
        hiddenKeywordDaoMock = mock(HiddenKeywordDao.class);
        userSearchHistoryDaoMock = mock(UserSearchHistoryDao.class);
        searchNewsService = new SearchNewsService(searchDaoMock, newsDaoMock, hiddenKeywordDaoMock, userSearchHistoryDaoMock);
    }

    @Test
    public void testSearchArticles_withDateAndSort() {
        List<NewsArticle> articles = List.of(createNewsArticle(1));
        when(searchDaoMock.searchArticles("java", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-05"), "likes")).thenReturn(articles);
        when(newsDaoMock.getAllCategory(1)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of());
        List<NewsArticle> result = searchNewsService.searchArticles(1, "java", "2023-01-01", "2023-01-05", "likes");
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchArticles_withDateOnly() {
        List<NewsArticle> articles = List.of(createNewsArticle(2));
        when(searchDaoMock.searchArticles("tech", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-05"))).thenReturn(articles);
        when(newsDaoMock.getAllCategory(2)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of());
        List<NewsArticle> result = searchNewsService.searchArticles(1, "tech", "2023-01-01", "2023-01-05", null);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchArticles_withSortOnly() {
        List<NewsArticle> articles = List.of(createNewsArticle(3));
        when(searchDaoMock.searchArticlesSorted("ai", "dislikes")).thenReturn(articles);
        when(newsDaoMock.getAllCategory(3)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of());
        List<NewsArticle> result = searchNewsService.searchArticles(1, "ai", null, null, "dislikes");
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchArticles_noSortOrDate() {
        List<NewsArticle> articles = List.of(createNewsArticle(4));
        when(searchDaoMock.searchArticles("news")).thenReturn(articles);
        when(newsDaoMock.getAllCategory(4)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of());
        List<NewsArticle> result = searchNewsService.searchArticles(1, "news", null, null, null);
        assertEquals(1, result.size());
    }

    @Test
    public void testSearchArticles_filtersBlockedKeyword() {
        List<NewsArticle> articles = List.of(createNewsArticle(5, "Fake News"));
        HiddenKeyword blocked = new HiddenKeyword();
        blocked.setKeyword("fake");
        when(searchDaoMock.searchArticles("fake")).thenReturn(articles);
        when(newsDaoMock.getAllCategory(5)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of(blocked));
        List<NewsArticle> result = searchNewsService.searchArticles(1, "fake", null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetRecentSearchKeywords_returnsList() {
        List<String> keywords = List.of("java", "ai", "news");
        when(userSearchHistoryDaoMock.getRecentSearchKeywords(10, 20)).thenReturn(keywords);
        List<String> result = searchNewsService.getRecentSearchKeywords(10);
        assertEquals(3, result.size());
    }

    private NewsArticle createNewsArticle(int id) {
        NewsArticle article = new NewsArticle();
        article.setId(id);
        article.setTitle("Sample Title");
        article.setDescription("Sample Description");
        article.setSnippet("Sample Snippet");
        article.setCategories(new ArrayList<>());
        return article;
    }

    private NewsArticle createNewsArticle(int id, String title) {
        NewsArticle article = new NewsArticle();
        article.setId(id);
        article.setTitle(title);
        article.setDescription("");
        article.setSnippet("");
        article.setCategories(new ArrayList<>());
        return article;
    }
}
