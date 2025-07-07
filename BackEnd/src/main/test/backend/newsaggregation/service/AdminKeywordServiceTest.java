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
    public void testDeleteKeyword_Success() {
        when(hiddenKeywordDao.deleteKeyword(1)).thenReturn(true);
        boolean result = adminKeywordService.deleteKeyword(1);
        assertTrue(result);
        verify(hiddenKeywordDao).deleteKeyword(1);
    }

    @Test
    public void testGetAllKeywords() {
        List<HiddenKeyword> mockKeywords = Arrays.asList(
                new HiddenKeyword(1, "fake", null),
                new HiddenKeyword(2, "rumor", null)
        );
        when(hiddenKeywordDao.getAllKeywords()).thenReturn(mockKeywords);
        List<HiddenKeyword> result = adminKeywordService.getAllKeywords();
        assertEquals(2, result.size());
        assertEquals("fake", result.get(0).getKeyword());
        verify(hiddenKeywordDao).getAllKeywords();
    }
}
