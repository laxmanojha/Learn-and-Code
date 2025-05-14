package com.itt.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;

public class TestCartService {

    @Mock
    private UserDao userDao;

    @Mock
    private CartDao cartDao;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchAllCartItems_ValidUserWithItems() {
        String username = "laxman";
        int userId = 1;
        int cartId = 10;
        List<CartItemDto> mockCartItems = Arrays.asList(new CartItemDto(), new CartItemDto());

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.getAllCartItems(cartId)).thenReturn(mockCartItems);

        List<CartItemDto> result = cartService.fetchAllCartItems(username);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFetchAllCartItems_ValidUserNoItems() {
        String username = "laxman";
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.getAllCartItems(cartId)).thenReturn(null);

        List<CartItemDto> result = cartService.fetchAllCartItems(username);
        assertNull(result);
    }

    @Test
    public void testFetchAllCartItems_InvalidUser() {
        String username = "InvalidUser";

        when(userDao.getUserIDByUsername(username)).thenReturn(-1);
        when(cartDao.getCartIDByUserID(-1)).thenReturn(-1);
        when(cartDao.getAllCartItems(-1)).thenReturn(null);

        List<CartItemDto> result = cartService.fetchAllCartItems(username);
        assertNull(result);
    }

    @Test
    public void testFetchAllCartItems_NullUsername() {
    	String username = null;

        when(userDao.getUserIDByUsername(username)).thenReturn(-1);
        when(cartDao.getCartIDByUserID(-1)).thenReturn(-1);
        when(cartDao.getAllCartItems(-1)).thenReturn(null);

        List<CartItemDto> result = cartService.fetchAllCartItems(username);
        assertNull(result);
    }

    @Test
    public void testFetchAllCartItems_EmptyUsername() {
        String username = "";

        when(userDao.getUserIDByUsername(username)).thenReturn(-1);
        when(cartDao.getCartIDByUserID(-1)).thenReturn(-1);
        when(cartDao.getAllCartItems(-1)).thenReturn(null);

        List<CartItemDto> result = cartService.fetchAllCartItems(username);
        assertNull(result);
    }

    @Test
    public void testFetchAllCartItems_UserDaoThrowsException() {
        String username = "laxman";

        when(userDao.getUserIDByUsername(username)).thenThrow(new RuntimeException("User DAO failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartService.fetchAllCartItems(username);
        });

        assertEquals("User DAO failure", ex.getMessage());
    }

    @Test
    public void testFetchAllCartItems_CartDaoCartIdThrowsException() {
        String username = "laxman";

        when(userDao.getUserIDByUsername(username)).thenReturn(1);
        when(cartDao.getCartIDByUserID(1)).thenThrow(new RuntimeException("Cart ID fetch failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartService.fetchAllCartItems(username);
        });

        assertEquals("Cart ID fetch failed", ex.getMessage());
    }

    @Test
    public void testFetchAllCartItems_CartDaoItemsThrowsException() {
        String username = "laxman";

        when(userDao.getUserIDByUsername(username)).thenReturn(1);
        when(cartDao.getCartIDByUserID(1)).thenReturn(10);
        when(cartDao.getAllCartItems(10)).thenThrow(new RuntimeException("Items fetch failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cartService.fetchAllCartItems(username);
        });

        assertEquals("Items fetch failed", ex.getMessage());
    }
}
