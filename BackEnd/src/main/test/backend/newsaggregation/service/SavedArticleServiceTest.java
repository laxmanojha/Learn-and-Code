package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.SavedArticleDao;
import backend.newsaggregation.model.HiddenKeyword;
import backend.newsaggregation.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SavedArticleServiceTest {

    private SavedArticleDao savedArticleDaoMock;
    private NewsDao newsDaoMock;
    private HiddenKeywordDao hiddenKeywordDaoMock;
    private SavedArticleService savedArticleService;

    @BeforeEach
    public void setup() {
        savedArticleDaoMock = mock(SavedArticleDao.class);
        newsDaoMock = mock(NewsDao.class);
        hiddenKeywordDaoMock = mock(HiddenKeywordDao.class);
        savedArticleService = new SavedArticleService(savedArticleDaoMock, newsDaoMock, hiddenKeywordDaoMock);
    }

    @Test
    public void testSaveArticle_notAlreadySaved_shouldSave() {
        when(savedArticleDaoMock.isArticleSavedByUser(1, 100)).thenReturn(false);
        when(savedArticleDaoMock.saveArticle(1, 100)).thenReturn(true);
        boolean result = savedArticleService.saveArticle(1, 100);
        assertTrue(result);
    }

    @Test
    public void testSaveArticle_alreadySaved_shouldNotSave() {
        when(savedArticleDaoMock.isArticleSavedByUser(1, 100)).thenReturn(true);
        boolean result = savedArticleService.saveArticle(1, 100);
        assertFalse(result);
    }

    @Test
    public void testDeleteSavedArticle_shouldReturnTrue() {
        when(savedArticleDaoMock.deleteSavedArticle(2, 200)).thenReturn(true);
        boolean result = savedArticleService.deleteSavedArticle(2, 200);
        assertTrue(result);
    }

    @Test
    public void testIsArticleSaved_true() {
        when(savedArticleDaoMock.isArticleSavedByUser(3, 300)).thenReturn(true);
        boolean result = savedArticleService.isArticleSaved(3, 300);
        assertTrue(result);
    }

    @Test
    public void testIsArticleSaved_false() {
        when(savedArticleDaoMock.isArticleSavedByUser(3, 301)).thenReturn(false);
        boolean result = savedArticleService.isArticleSaved(3, 301);
        assertFalse(result);
    }

    @Test
    public void testGetSavedArticlesByUser_noBlockedKeywords() {
        NewsArticle article = createNewsArticle(1, "Clean Title");
        when(savedArticleDaoMock.getSavedArticlesByUser(4)).thenReturn(List.of(article));
        when(newsDaoMock.getAllCategory(1)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of());

        List<NewsArticle> result = savedArticleService.getSavedArticlesByUser(4);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetSavedArticlesByUser_containsBlockedKeyword_shouldBeFilteredOut() {
        NewsArticle article = createNewsArticle(2, "This contains banned content");
        HiddenKeyword keyword = new HiddenKeyword();
        keyword.setKeyword("banned");

        when(savedArticleDaoMock.getSavedArticlesByUser(5)).thenReturn(List.of(article));
        when(newsDaoMock.getAllCategory(2)).thenReturn(List.of());
        when(hiddenKeywordDaoMock.getAllKeywords()).thenReturn(List.of(keyword));

        List<NewsArticle> result = savedArticleService.getSavedArticlesByUser(5);
        assertTrue(result.isEmpty());
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
