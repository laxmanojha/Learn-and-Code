package com.itt.ecommerce.dao;

import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.util.DatabaseConfig;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCartDao {

    @Mock Connection mockConnection;
    @Mock PreparedStatement mockPreparedStatement;
    @Mock ResultSet mockResultSet;

    @InjectMocks CartDao cartDao;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        DatabaseConfig.setConnection(mockConnection);
    }

    @Test
    public void testGetCartIDByUserID_WhenCartExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("cart_id")).thenReturn(101);

        int cartId = cartDao.getCartIDByUserID(1);
        assertEquals(101, cartId);
    }

    @Test
    public void testGetCartIDByUserID_WhenCartDoesNotExist() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        int cartId = cartDao.getCartIDByUserID(999);
        assertEquals(-1, cartId);
    }

    @Test
    public void testGetUserIDByCartID_WhenCartExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("user_id")).thenReturn(5);

        int userId = cartDao.getUserIDByCartID(101);
        assertEquals(5, userId);
    }

    @Test
    public void testAddUserIdToCart_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = cartDao.addUserIdToCart(5);
        assertTrue(result);
    }

    @Test
    public void testAddUserIdToCart_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = cartDao.addUserIdToCart(5);
        assertFalse(result);
    }

    @Test
    public void testGetAllCartItems_WhenItemsExist() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("c.cart_item_id")).thenReturn(1);
        when(mockResultSet.getInt("c.cart_id")).thenReturn(100);
        when(mockResultSet.getInt("p.product_id")).thenReturn(200);
        when(mockResultSet.getInt("c.quantity")).thenReturn(3);
        when(mockResultSet.getString("p.product_name")).thenReturn("Mobile");
        when(mockResultSet.getFloat("p.price")).thenReturn(5000f);

        List<CartItemDto> items = cartDao.getAllCartItems(100);
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void testGetAllCartItems_WhenNoItems() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        List<CartItemDto> items = cartDao.getAllCartItems(999);
        assertTrue(items.isEmpty());
    }

    @Test
    public void testAddItemToCart_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = cartDao.addItemToCart(1, 101, 3);
        assertTrue(result);
    }

    @Test
    public void testAddItemToCart_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = cartDao.addItemToCart(1, 101, 3);
        assertFalse(result);
    }

    @Test
    public void testRemoveItemFromCart_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = cartDao.removeItemFromCart(1, 101);
        assertTrue(result);
    }

    @Test
    public void testRemoveItemFromCart_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = cartDao.removeItemFromCart(1, 101);
        assertFalse(result);
    }

    @Test
    public void testUpdateItemInCart_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = cartDao.updateItemInCart(5, 1, 101);
        assertTrue(result);
    }

    @Test
    public void testUpdateItemInCart_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = cartDao.updateItemInCart(5, 1, 101);
        assertFalse(result);
    }

    @Test
    public void testRemoveAllItemsFromCart_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = cartDao.removeAllItemsFromCart(1);
        assertTrue(result);
    }

    @Test
    public void testRemoveAllItemsFromCart_Failure() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = cartDao.removeAllItemsFromCart(1);
        assertFalse(result);
    }
}

