package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.*;
import backend.newsaggregation.model.Category;
import backend.newsaggregation.model.NotificationPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private UserDao userDaoMock;
    private CategoryDao categoryDaoMock;
    private NotificationCategoryPrefDao categoryPrefDaoMock;
    private NotificationKeywordPrefDao keywordPrefDaoMock;
    private NotificationDao notificationDaoMock;
    private NewsDao newsDaoMock;
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        userDaoMock = mock(UserDao.class);
        categoryDaoMock = mock(CategoryDao.class);
        categoryPrefDaoMock = mock(NotificationCategoryPrefDao.class);
        keywordPrefDaoMock = mock(NotificationKeywordPrefDao.class);
        notificationDaoMock = mock(NotificationDao.class);
        newsDaoMock = mock(NewsDao.class);
        notificationService = new NotificationService(userDaoMock, categoryDaoMock, categoryPrefDaoMock, keywordPrefDaoMock, notificationDaoMock, newsDaoMock);
    }

    @Test
    public void testGetAllPreferences_withSavedCategoryPrefs_shouldIncludeKeywordPref() {
        int userId = 2;
        NotificationPreference keywordPref = new NotificationPreference();
        NotificationPreference catPref = new NotificationPreference();
        List<NotificationPreference> categoryPrefs = new ArrayList<>();
        categoryPrefs.add(catPref);

        when(categoryPrefDaoMock.getCategoryPreferencesByUser(userId)).thenReturn(categoryPrefs);
        when(keywordPrefDaoMock.getPreferencesByUser(userId)).thenReturn(keywordPref);

        List<NotificationPreference> result = notificationService.getAllPreferences(userId);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllPreferences_withNoCategoryPrefs_shouldCreateFromCategory() {
        int userId = 3;
        Category category = new Category();
        category.setId(10);
        category.setName("Politics");

        when(categoryPrefDaoMock.getCategoryPreferencesByUser(userId)).thenReturn(new ArrayList<>());
        when(categoryDaoMock.getAllCategory()).thenReturn(List.of(category));
        when(keywordPrefDaoMock.getPreferencesByUser(userId)).thenReturn(new NotificationPreference());

        List<NotificationPreference> result = notificationService.getAllPreferences(userId);
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateCategoryConfig_addKeywords_shouldReturnTrue() {
        when(categoryPrefDaoMock.addCategoryPreference(1, 100, List.of("ai"))).thenReturn(true);
        boolean result = notificationService.updateCategoryConfig(1, 100, List.of("ai"));
        assertTrue(result);
    }

    @Test
    public void testUpdateCategoryConfig_removeCategory_shouldReturnTrue() {
        when(categoryPrefDaoMock.removeCategoryPreference(1, 101)).thenReturn(true);
        boolean result = notificationService.updateCategoryConfig(1, 101);
        assertTrue(result);
    }

    @Test
    public void testAddKeywords_shouldReturnTrue() {
        when(keywordPrefDaoMock.addKeywordPreference(5, List.of("deep", "machine"))).thenReturn(true);
        boolean result = notificationService.addKeywords(5, List.of("deep", "machine"));
        assertTrue(result);
    }

    @Test
    public void testRemoveKeywordsAll_shouldReturnTrue() {
        when(keywordPrefDaoMock.removeAllKeywordPreference(6)).thenReturn(true);
        boolean result = notificationService.removeKeywords(6);
        assertTrue(result);
    }

    @Test
    public void testRemoveSpecificKeywords_shouldReturnTrue() {
        when(keywordPrefDaoMock.removeKeywordPreference(7, List.of("tech"))).thenReturn(true);
        boolean result = notificationService.removeKeywords(7, List.of("tech"));
        assertTrue(result);
    }

    @Test
    public void testGetCategoryPreferences_shouldReturnList() {
        List<NotificationPreference> list = List.of(new NotificationPreference());
        when(categoryPrefDaoMock.getCategoryPreferencesByUser(8)).thenReturn(list);
        List<NotificationPreference> result = notificationService.getCategoryPreferences(8);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetKeywordPreferences_shouldReturnPref() {
        NotificationPreference pref = new NotificationPreference();
        when(keywordPrefDaoMock.getPreferencesByUser(9)).thenReturn(pref);
        NotificationPreference result = notificationService.getKeywordPreferences(9);
        assertEquals(pref, result);
    }

    @Test
    public void testUpdateKeywordStatus_shouldReturnTrue() {
        when(keywordPrefDaoMock.updateKeywordPreference(10, null, true)).thenReturn(true);
        boolean result = notificationService.updateKeywordStatus(10, "ai", true);
        assertTrue(result);
    }
}
