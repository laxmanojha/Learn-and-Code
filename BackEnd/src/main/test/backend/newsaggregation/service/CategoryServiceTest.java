package backend.newsaggregation.service;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryDao);
    }

    @Test
    public void testAddCategory_Success() throws Exception {
        when(categoryDao.addCategory("Tech")).thenReturn(true);
        boolean result = categoryService.addCategory("Tech");
        assertTrue(result);
        verify(categoryDao).addCategory("Tech");
    }

    @Test
    public void testAddCategory_Failure() throws Exception {
        when(categoryDao.addCategory("Tech")).thenReturn(false);
        boolean result = categoryService.addCategory("Tech");
        assertFalse(result);
        verify(categoryDao).addCategory("Tech");
    }

    @Test
    public void testAddCategory_Exception() {
        try {
            when(categoryDao.addCategory("Science")).thenThrow(new RuntimeException("DB Error"));
            assertThrows(RuntimeException.class, () -> categoryService.addCategory("Science"));
            verify(categoryDao).addCategory("Science");
        } catch (Exception e) {
            fail("Should not reach catch block");
        }
    }

    @Test
    public void testGetAllCategory_WithData() {
        List<Category> mockCategories = Arrays.asList(
                new Category(1, "Politics"),
                new Category(2, "Technology")
        );
        when(categoryDao.getAllCategory()).thenReturn(mockCategories);
        List<Category> result = categoryService.getAllCategory();
        assertEquals(2, result.size());
        assertEquals("Politics", result.get(0).getName());
        verify(categoryDao).getAllCategory();
    }

    @Test
    public void testGetAllCategory_EmptyList() {
        when(categoryDao.getAllCategory()).thenReturn(Collections.emptyList());
        List<Category> result = categoryService.getAllCategory();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryDao).getAllCategory();
    }

    @Test
    public void testHideCategory_Success() {
        when(categoryDao.hideCategory(5)).thenReturn(true);
        boolean result = categoryService.hideCategory(5);
        assertTrue(result);
        verify(categoryDao).hideCategory(5);
    }

    @Test
    public void testHideCategory_Failure() {
        when(categoryDao.hideCategory(99)).thenReturn(false);
        boolean result = categoryService.hideCategory(99);
        assertFalse(result);
        verify(categoryDao).hideCategory(99);
    }

    @Test
    public void testUnhideCategory_Success() {
        when(categoryDao.unhideCategory(3)).thenReturn(true);
        boolean result = categoryService.unhideCategory(3);
        assertTrue(result);
        verify(categoryDao).unhideCategory(3);
    }

    @Test
    public void testUnhideCategory_Failure() {
        when(categoryDao.unhideCategory(999)).thenReturn(false);
        boolean result = categoryService.unhideCategory(999);
        assertFalse(result);
        verify(categoryDao).unhideCategory(999);
    }

    @Test
    public void testSingletonInstance_NotNull() {
        CategoryService instance = CategoryService.getInstance();
        assertNotNull(instance);
    }
}
