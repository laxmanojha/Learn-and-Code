package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.NewsReactionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsReactionServiceTest {

    @Mock
    private NewsReactionDao reactionDao;

    @InjectMocks
    private NewsReactionService newsReactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        newsReactionService = new NewsReactionService(reactionDao);
    }

    @Test
    public void testReactToArticle_Like_Success() {
        int userId = 1;
        int newsId = 101;
        String reaction = "like";

        when(reactionDao.reactToArticle(userId, newsId, "like")).thenReturn(true);

        boolean result = newsReactionService.reactToArticle(userId, newsId, reaction);
        assertTrue(result);
        verify(reactionDao).reactToArticle(userId, newsId, "like");
    }

    @Test
    public void testReactToArticle_Dislike_Success() {
        int userId = 2;
        int newsId = 102;
        String reaction = "dislike";

        when(reactionDao.reactToArticle(userId, newsId, "dislike")).thenReturn(true);

        boolean result = newsReactionService.reactToArticle(userId, newsId, reaction);
        assertTrue(result);
        verify(reactionDao).reactToArticle(userId, newsId, "dislike");
    }

    @Test
    public void testReactToArticle_InvalidReaction_ThrowsException() {
        int userId = 3;
        int newsId = 103;
        String reaction = "love";

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> newsReactionService.reactToArticle(userId, newsId, reaction)
        );

        assertEquals("Invalid reaction type. Only 'like' or 'dislike' allowed.", exception.getMessage());
        verify(reactionDao, never()).reactToArticle(anyInt(), anyInt(), anyString());
    }

    @Test
    public void testReactToArticle_Like_CaseInsensitive() {
        int userId = 4;
        int newsId = 104;
        String reaction = "LiKe";

        when(reactionDao.reactToArticle(userId, newsId, "like")).thenReturn(true);

        boolean result = newsReactionService.reactToArticle(userId, newsId, reaction);
        assertTrue(result);
        verify(reactionDao).reactToArticle(userId, newsId, "like");
    }
}
