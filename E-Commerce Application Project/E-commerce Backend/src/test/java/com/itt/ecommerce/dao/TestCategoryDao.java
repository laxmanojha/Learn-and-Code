package com.itt.ecommerce.dao;

import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCategoryDao {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockPreparedStatement;
    @Mock ResultSet mockResultSet;

    @InjectMocks CategoryDao categoryDao;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        DatabaseConfig.setConnection(mockConnection);
        categoryDao = new CategoryDao();
    }

    @Test
    public void testGetAllCategories_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("category_id")).thenReturn(1, 2);
        when(mockResultSet.getString("category_name")).thenReturn("Electronics", "Books");

        ArrayList<CategoryDto> categories = categoryDao.getAllCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Electronics", categories.get(0).getCategory_name());
        assertEquals("Books", categories.get(1).getCategory_name());
    }

    @Test
    public void testGetAllCategories_EmptyList() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        ArrayList<CategoryDto> categories = categoryDao.getAllCategories();

        assertNotNull(categories);
        assertEquals(0, categories.size());
    }

    @Test
    public void testGetAllCategories_Exception() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        ArrayList<CategoryDto> categories = categoryDao.getAllCategories();

        assertNull(categories);
    }
}
