package backend.newsaggregation.service;

import backend.newsaggregation.constants.StaticConfigurations;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.dao.interfaces.NewsReportDao;
import backend.newsaggregation.model.NewsArticleReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsReportServiceTest {

    @Mock
    private NewsReportDao newsReportDao;

    @Mock
    private NewsDao newsDao;

    @Mock
    private StaticConfigurations configurations;

    @InjectMocks
    private NewsReportService newsReportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        newsReportService = new NewsReportService(newsReportDao, newsDao, configurations);
    }

    @Test
    public void testReportArticle_HidesArticleWhenThresholdExceeded() {
        int userId = 1, newsId = 10;
        String comment = "Fake News";

        when(newsReportDao.reportArticle(userId, newsId, comment)).thenReturn(true);
        when(newsDao.getReportCount(newsId)).thenReturn(5);
        when(configurations.getReportThreshold()).thenReturn(3);
        when(newsDao.hideArticle(newsId)).thenReturn(true);

        boolean result = newsReportService.reportArticle(userId, newsId, comment);
        assertTrue(result);
        verify(newsDao).hideArticle(newsId);
    }

    @Test
    public void testReportArticle_DoesNotHideWhenBelowThreshold() {
        int userId = 2, newsId = 20;
        String comment = "Inaccurate";

        when(newsReportDao.reportArticle(userId, newsId, comment)).thenReturn(true);
        when(newsDao.getReportCount(newsId)).thenReturn(1);
        when(configurations.getReportThreshold()).thenReturn(5);

        boolean result = newsReportService.reportArticle(userId, newsId, comment);
        assertTrue(result);
        verify(newsDao, never()).hideArticle(newsId);
    }

    @Test
    public void testReportExceededThreshold_True() {
        int newsId = 5;

        when(newsDao.getReportCount(newsId)).thenReturn(10);
        when(configurations.getReportThreshold()).thenReturn(5);

        assertTrue(newsReportService.reportExceededThreshold(newsId));
    }

    @Test
    public void testReportExceededThreshold_False() {
        int newsId = 6;

        when(newsDao.getReportCount(newsId)).thenReturn(2);
        when(configurations.getReportThreshold()).thenReturn(3);

        assertFalse(newsReportService.reportExceededThreshold(newsId));
    }

    @Test
    public void testHideArticle() {
        when(newsDao.hideArticle(100)).thenReturn(true);
        assertTrue(newsReportService.hideArticle(100));
    }

    @Test
    public void testUnHideArticle() {
        when(newsDao.unhideArticle(101)).thenReturn(true);
        assertTrue(newsReportService.unHideArticle(101));
    }

    @Test
    public void testGetNewsArticleReport() {
        List<NewsArticleReport> reports = Arrays.asList(
                new NewsArticleReport(1, 1, 100, "Misleading", null),
                new NewsArticleReport(2, 2, 101, "Offensive", null)
        );

        when(newsReportDao.getReportedArticles()).thenReturn(reports);

        List<NewsArticleReport> result = newsReportService.getNewsArticleReport();
        assertEquals(2, result.size());
        assertEquals("Misleading", result.get(0).getReason());
    }
}
