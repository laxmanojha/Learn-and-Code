package com.itt.ecommerce.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.OrderDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestOrderService {

	@Mock
    private UserDao userDao;
	@Mock
    private OrderDao orderDao;
    @Mock
    private ProductDao productDao;
    @Mock
    private CartDao cartDao;
    
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCompleteOrder_NullCartItems() {
        List<CartItemDto> cartItems = null;
        String result = orderService.completeOrder(cartItems);
        assertEquals("0:No cart items received to place an order", result);
    }

    @Test
    public void testCompleteOrder_EmptyCartItems() {
        List<CartItemDto> cartItems = List.of();
        String result = orderService.completeOrder(cartItems);
        assertEquals("0:No cart items received to place an order", result);
    }

    @Test
    public void testCompleteOrder_OrderCreationFailed() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(false);

        String result = orderService.completeOrder(cartItems);
        assertEquals("0:Order creation failed.", result);
    }

    @Test
    public void testCompleteOrder_SuccessfulOrderCreationAndItemAddition() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(true);
        when(orderDao.getOrderIdByUserId(anyInt())).thenReturn(1);
        when(orderDao.addOrderItemDetails(anyInt(), anyList())).thenReturn(true);
        when(productDao.updateProductStockQuantity(anyList())).thenReturn(true);
        when(cartDao.removeAllItemsFromCart(anyInt())).thenReturn(true);

        String result = orderService.completeOrder(cartItems);
        assertEquals("1:Item order completion successful.", result);
    }

    @Test
    public void testCompleteOrder_ItemAdditionFailed() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(true);
        when(orderDao.getOrderIdByUserId(anyInt())).thenReturn(1);
        when(orderDao.addOrderItemDetails(anyInt(), anyList())).thenReturn(false);

        String result = orderService.completeOrder(cartItems);
        assertNull(result);
    }

    @Test
    public void testCompleteOrder_StockUpdateFailed() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(true);
        when(orderDao.getOrderIdByUserId(anyInt())).thenReturn(1);
        when(orderDao.addOrderItemDetails(anyInt(), anyList())).thenReturn(true);
        when(productDao.updateProductStockQuantity(anyList())).thenReturn(false);

        String result = orderService.completeOrder(cartItems);
        assertEquals("0:Stock quantity is not get updated.", result);
    }

    @Test
    public void testCompleteOrder_CartItemsNotDeleted() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(true);
        when(orderDao.getOrderIdByUserId(anyInt())).thenReturn(1);
        when(orderDao.addOrderItemDetails(anyInt(), anyList())).thenReturn(true);
        when(productDao.updateProductStockQuantity(anyList())).thenReturn(true);
        when(cartDao.removeAllItemsFromCart(anyInt())).thenReturn(false);

        String result = orderService.completeOrder(cartItems);
        assertEquals("0:Stock quantity is not get updated.", result);
    }

    @Test
    public void testCompleteOrder_InvalidUser() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());

        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenThrow(new IllegalArgumentException("Invalid User"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.completeOrder(cartItems);
        });
        assertEquals("Invalid User", exception.getMessage());
    }

    @Test
    public void testCompleteOrder_MixedSuccess() {
        List<CartItemDto> cartItems = List.of(new CartItemDto());
        when(orderDao.addOrderDetails(anyInt(), anyFloat())).thenReturn(true);
        when(orderDao.getOrderIdByUserId(anyInt())).thenReturn(1);
        when(orderDao.addOrderItemDetails(anyInt(), anyList())).thenReturn(true);
        when(productDao.updateProductStockQuantity(anyList())).thenReturn(false);
        when(cartDao.removeAllItemsFromCart(anyInt())).thenReturn(true);

        String result = orderService.completeOrder(cartItems);
        assertEquals("0:Stock quantity is not get updated.", result);
    }
    
    @Test
    public void testGetOrderHistory_ValidUserWithOrders() {
        String username = "john_doe";
        int userId = 101;

        List<OrderHistoryDto> mockOrderList = new ArrayList<>();
        mockOrderList.add(new OrderHistoryDto());

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(orderDao.getOrderHistory(userId)).thenReturn(mockOrderList);

        List<OrderHistoryDto> result = orderService.getOrderHistory(username);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetOrderHistory_ValidUserNoOrders() {
        String username = "jane_doe";
        int userId = 102;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(orderDao.getOrderHistory(userId)).thenReturn(Collections.emptyList());

        List<OrderHistoryDto> result = orderService.getOrderHistory(username);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOrderHistory_UserNotFound() {
        String username = "ghost_user";
        int invalidUserId = -1;

        when(userDao.getUserIDByUsername(username)).thenReturn(invalidUserId);
        when(orderDao.getOrderHistory(invalidUserId)).thenReturn(Collections.emptyList());

        List<OrderHistoryDto> result = orderService.getOrderHistory(username);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOrderHistory_NullUsername() {
    	
    	String username = null;
    	int userId = -1;
    	
    	when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(orderDao.getOrderHistory(userId)).thenReturn(null);

        List<OrderHistoryDto> result = orderService.getOrderHistory(username);
        assertNull(result);
    }

    @Test
    public void testGetOrderHistory_EmptyUsername() {
        String username = "";
        int userId = -1;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(orderDao.getOrderHistory(userId)).thenReturn(Collections.emptyList());

        List<OrderHistoryDto> result = orderService.getOrderHistory(username);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOrderHistory_UserDaoThrowsException() {
        String username = "userX";
        when(userDao.getUserIDByUsername(username)).thenThrow(new RuntimeException("User DAO error"));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderHistory(username);
        });

        assertEquals("User DAO error", ex.getMessage());
    }

    @Test
    public void testGetOrderHistory_OrderDaoThrowsException() {
        String username = "userY";
        int userId = 200;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(orderDao.getOrderHistory(userId)).thenThrow(new RuntimeException("Order DAO error"));

        Exception ex = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderHistory(username);
        });

        assertEquals("Order DAO error", ex.getMessage());
    }
}
