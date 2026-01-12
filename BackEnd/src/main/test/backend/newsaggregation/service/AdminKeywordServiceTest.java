package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.HiddenKeywordDao;
import backend.newsaggregation.model.HiddenKeyword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminKeywordServiceTest {

    @Mock
    private HiddenKeywordDao hiddenKeywordDao;

    @InjectMocks
    private AdminKeywordService adminKeywordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adminKeywordService = new AdminKeywordService(hiddenKeywordDao);
    }

    @Test
    public void testAddKeyword_Success() {
        when(hiddenKeywordDao.addKeyword("fake")).thenReturn(true);
        boolean result = adminKeywordService.addKeyword("fake");
        assertTrue(result);
        verify(hiddenKeywordDao).addKeyword("fake");
    }

    @Test
    public void testAddKeyword_Failure() {
        when(hiddenKeywordDao.addKeyword("banned")).thenReturn(false);
        boolean result = adminKeywordService.addKeyword("banned");
        assertFalse(result);
        verify(hiddenKeywordDao).addKeyword("banned");
    }

    @Test
    public void testDeleteKeyword_Success() {
        when(hiddenKeywordDao.deleteKeyword(1)).thenReturn(true);
        boolean result = adminKeywordService.deleteKeyword(1);
        assertTrue(result);
        verify(hiddenKeywordDao).deleteKeyword(1);
    }

    @Test
    public void testDeleteKeyword_Failure() {
        when(hiddenKeywordDao.deleteKeyword(999)).thenReturn(false);
        boolean result = adminKeywordService.deleteKeyword(999);
        assertFalse(result);
        verify(hiddenKeywordDao).deleteKeyword(999);
    }

    @Test
    public void testGetAllKeywords_ReturnsList() {
        List<HiddenKeyword> mockKeywords = Arrays.asList(
                new HiddenKeyword(1, "fake", null),
                new HiddenKeyword(2, "rumor", null)
        );
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(mockKeywords);

        List<HiddenKeyword> result = adminKeywordService.getAllKeywords();

        assertEquals(2, result.size());
        assertEquals("fake", result.get(0).getKeyword());
        assertEquals("rumor", result.get(1).getKeyword());
        verify(hiddenKeywordDao).getAllKeywords();
    }

    @Test
    public void testGetAllKeywords_EmptyList() {
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(List.of());

        List<HiddenKeyword> result = adminKeywordService.getAllKeywords();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(hiddenKeywordDao).getAllKeywords();
    }

    @Test
    public void testSingletonInstance_NotNull() {
        AdminKeywordService instance = AdminKeywordService.getInstance();
        assertNotNull(instance);
    }
}
