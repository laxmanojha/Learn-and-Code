package com.itt.ecommerce.dao;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestProductDao {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockPreparedStatement;
    @Mock ResultSet mockResultSet;

    @InjectMocks ProductDao productDao;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        DatabaseConfig.setConnection(mockConnection);
        productDao = new ProductDao();
    }

    @Test
    public void testGetAllProductsByCategoryId_ReturnsList() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("product_id")).thenReturn(1);
        when(mockResultSet.getString("product_name")).thenReturn("Phone");
        when(mockResultSet.getInt("category_id")).thenReturn(2);
        when(mockResultSet.getFloat("price")).thenReturn(499.99f);
        when(mockResultSet.getInt("stock_quantity")).thenReturn(10);

        List<ProductDto> products = productDao.getAllProductsByCategoryId(2);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Phone", products.get(0).getProduct_name());
    }

    @Test
    public void testGetProductById_Found() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("product_id")).thenReturn(1);
        when(mockResultSet.getString("product_name")).thenReturn("Laptop");
        when(mockResultSet.getInt("category_id")).thenReturn(1);
        when(mockResultSet.getFloat("price")).thenReturn(999.99f);
        when(mockResultSet.getInt("stock_quantity")).thenReturn(5);

        ProductDto product = productDao.getProductById(1);

        assertNotNull(product);
        assertEquals("Laptop", product.getProduct_name());
    }

    @Test
    public void testGetProductById_NotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        ProductDto product = productDao.getProductById(99);

        assertNull(product);
    }

    @Test
    public void testUpdateProductStockQuantity_Success() throws Exception {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto(1, 2, 3, 1, "Product1", 100f));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeBatch()).thenReturn(new int[]{1});

        boolean result = productDao.updateProductStockQuantity(cartItems);

        assertTrue(result);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testUpdateProductStockQuantity_FailureOnBatch() throws Exception {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto(1, 2, 3, 1, "Product1", 100f));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeBatch()).thenReturn(new int[]{0});

        boolean result = productDao.updateProductStockQuantity(cartItems);

        assertFalse(result);
        verify(mockConnection).rollback();
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).setAutoCommit(true);
    }
}
