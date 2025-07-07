package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.ExternalServerDao;
import backend.newsaggregation.dao.interfaces.NewsDao;
import backend.newsaggregation.model.ExternalServer;
import backend.newsaggregation.model.NewsArticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExternalServerServiceTest {

    @Mock
    private ExternalServerDao serverDao;

    @Mock
    private NewsDao newsDao;

    @InjectMocks
    private ExternalServerService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ExternalServerService(serverDao, newsDao);
    }

    @Test
    public void testGetAllServersBasicDetails() {
        List<ExternalServer> expected = Arrays.asList(new ExternalServer(), new ExternalServer());
        when(serverDao.getAllServersBasicDetails()).thenReturn(expected);

        List<ExternalServer> result = service.getAllServersBasicDetails();

        assertEquals(2, result.size());
        verify(serverDao).getAllServersBasicDetails();
    }

    @Test
    public void testGetAllServersWithApiKeys() {
        List<ExternalServer> expected = Arrays.asList(new ExternalServer());
        when(serverDao.getAllServersWithApiKeys()).thenReturn(expected);

        List<ExternalServer> result = service.getAllServersWithApiKeys();

        assertEquals(1, result.size());
        verify(serverDao).getAllServersWithApiKeys();
    }

    @Test
    public void testGetServerById() {
        ExternalServer expected = new ExternalServer();
        when(serverDao.getServerById(1)).thenReturn(expected);

        ExternalServer result = service.getServerById(1);

        assertNotNull(result);
        verify(serverDao).getServerById(1);
    }

    @Test
    public void testUpdateApiKey() {
        when(serverDao.updateApiKey(1, "new-key")).thenReturn(true);

        boolean result = service.updateApiKey(1, "new-key");

        assertTrue(result);
        verify(serverDao).updateApiKey(1, "new-key");
    }

    @Test
    public void testSaveDataFromApiToDB() {
        NewsArticle article = new NewsArticle();
        article.setCategories(Arrays.asList("Tech", "AI"));

        when(newsDao.saveNews(article)).thenReturn(100);
        when(newsDao.getOrInsertCategoryId("Tech")).thenReturn(1);
        when(newsDao.getOrInsertCategoryId("AI")).thenReturn(2);
        when(newsDao.insertNewsCategoryMapping(100, 1)).thenReturn(true);
        when(newsDao.insertNewsCategoryMapping(100, 2)).thenReturn(true);

        service.saveDataFromApiToDB(Collections.singletonList(article));

        verify(newsDao).saveNews(article);
        verify(newsDao).getOrInsertCategoryId("Tech");
        verify(newsDao).getOrInsertCategoryId("AI");
        verify(newsDao).insertNewsCategoryMapping(100, 1);
        verify(newsDao).insertNewsCategoryMapping(100, 2);
    }

    @Test
    public void testUpdateServerStatus_ServerFound_Active() {
        ExternalServer server = new ExternalServer();
        server.setId(10);
        when(serverDao.getServerByName("NewsAPI")).thenReturn(server);
        when(serverDao.updateServerStatusAndLastAccess(10, 1)).thenReturn(true);

        service.updateServerStatus("NewsAPI", true);

        verify(serverDao).updateServerStatusAndLastAccess(10, 1);
    }

    @Test
    public void testUpdateServerStatus_ServerNotFound() {
        when(serverDao.getServerByName("InvalidServer")).thenReturn(null);

        service.updateServerStatus("InvalidServer", false);

        verify(serverDao, never()).updateServerStatusAndLastAccess(anyInt(), anyInt());
    }

    @Test
    public void testGetApiKeyByServerName() {
        ExternalServer server = new ExternalServer();
        server.setApiKey("xyz123");
        when(serverDao.getServerByName("NewsAPI")).thenReturn(server);

        String result = service.getApiKeyByServerName("NewsAPI");

        assertEquals("xyz123", result);
        verify(serverDao).getServerByName("NewsAPI");
    }
}
