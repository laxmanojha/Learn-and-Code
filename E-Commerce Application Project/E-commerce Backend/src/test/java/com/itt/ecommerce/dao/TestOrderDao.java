package com.itt.ecommerce.dao;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;
import com.itt.ecommerce.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestOrderDao {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockPreparedStatement;
    @Mock ResultSet mockResultSet;

    @InjectMocks OrderDao orderDao;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        DatabaseConfig.setConnection(mockConnection);
        orderDao = new OrderDao();
    }

    @Test
    public void testGetOrderIdByUserId_Found() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("order_id")).thenReturn(1001);

        int result = orderDao.getOrderIdByUserId(1);

        assertEquals(1001, result);
    }

    @Test
    public void testGetOrderIdByUserId_NotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        int result = orderDao.getOrderIdByUserId(1);

        assertEquals(-1, result);
    }

    @Test
    public void testAddOrderDetails_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = orderDao.addOrderDetails(1, 500.0f);

        assertTrue(result);
    }

    @Test
    public void testAddOrderDetails_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = orderDao.addOrderDetails(1, 500.0f);

        assertFalse(result);
    }

    @Test
    public void testAddOrderItemDetails_Success() throws Exception {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto(1, 2, 3, 1, "Product1", 100f));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeBatch()).thenReturn(new int[]{1});

        boolean result = orderDao.addOrderItemDetails(101, cartItems);

        assertTrue(result);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testAddOrderItemDetails_FailureOnBatch() throws Exception {
        List<CartItemDto> cartItems = new ArrayList<>();
        cartItems.add(new CartItemDto(1, 2, 3, 1, "Product1", 100f));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeBatch()).thenReturn(new int[]{0});

        boolean result = orderDao.addOrderItemDetails(101, cartItems);

        assertFalse(result);
        verify(mockConnection).rollback();
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).setAutoCommit(true);
    }

    @Test
    public void testGetOrderHistory_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("c.category_name")).thenReturn("Electronics");
        when(mockResultSet.getString("p.product_name")).thenReturn("Phone");
        when(mockResultSet.getFloat("p.price")).thenReturn(500f);
        when(mockResultSet.getInt("oi.quantity")).thenReturn(2);
        when(mockResultSet.getFloat("quantities_total_price")).thenReturn(1000f);
        when(mockResultSet.getTimestamp("o.order_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<OrderHistoryDto> result = orderDao.getOrderHistory(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getProductName());
    }

    @Test
    public void testGetOrderHistory_EmptyResult() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<OrderHistoryDto> result = orderDao.getOrderHistory(1);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
