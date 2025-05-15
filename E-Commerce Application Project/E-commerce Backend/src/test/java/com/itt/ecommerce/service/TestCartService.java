package com.itt.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.itt.ecommerce.dao.CartDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dao.UserDao;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.ProductDto;

public class TestCartService {

    @Mock
    private UserDao userDao;

    @Mock
    private CartDao cartDao;
    
    @Mock
    private ProductDao productDao;

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
    
    @Test
    public void testAddToCart_Successful() {
        String username = "laxman";
        int productId = 1;
        int quantity = 2;

        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 5);

        when(productDao.getProductById(productId)).thenReturn(product);
        when(cartService.getCartId(username)).thenReturn(101);
        when(cartDao.addItemToCart(101, productId, quantity)).thenReturn(true);

        String result = cartService.addToCart(username, productId, quantity);
        assertEquals("1:Laptop has been added to your cart successfully.", result);
    }

    @Test
    public void testAddToCart_InvalidProductId() {
        when(productDao.getProductById(99)).thenReturn(null);

        String result = cartService.addToCart("laxman", 99, 1);
        assertEquals("0:Entered product id is not valid", result);
    }

    @Test
    public void testAddToCart_QuantityExceedsStock() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 3);

        when(productDao.getProductById(1)).thenReturn(product);

        String result = cartService.addToCart("laxman", 1, 5);
        assertEquals("0:Quantity can not be more than available stock quantity", result);
    }

    @Test
    public void testAddToCart_CartCreationFails() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 10);

        when(productDao.getProductById(1)).thenReturn(product);
        when(cartService.getCartId("laxman")).thenReturn(-1);

        String result = cartService.addToCart("laxman", 1, 2);
        assertEquals("0:Unable to create cart for laxman", result);
    }

    @Test
    public void testAddToCart_AddItemFails() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 10);

        when(productDao.getProductById(1)).thenReturn(product);
        when(cartService.getCartId("laxman")).thenReturn(101);
        when(cartDao.addItemToCart(101, 1, 2)).thenReturn(false);

        String result = cartService.addToCart("laxman", 1, 2);
        assertEquals("0:Failed in adding Laptop to cart.", result);
    }

    @Test
    public void testAddToCart_UsernameIsNull() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 10);

        when(productDao.getProductById(1)).thenReturn(product);
        when(cartService.getCartId(null)).thenReturn(-1);

        String result = cartService.addToCart(null, 1, 2);
        assertEquals("0:Unable to create cart for null", result);
    }

    @Test
    public void testAddToCart_ProductDaoThrowsException() {
        when(productDao.getProductById(1)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            cartService.addToCart("laxman", 1, 2);
        });
    }

    @Test
    public void testAddToCart_CartIdThrowsException() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 10);

        when(productDao.getProductById(1)).thenReturn(product);
        when(cartService.getCartId("laxman")).thenThrow(new RuntimeException("Cart ID fetch error"));

        assertThrows(RuntimeException.class, () -> {
            cartService.addToCart("laxman", 1, 2);
        });
    }

    @Test
    public void testAddToCart_AddItemToCartThrowsException() {
        ProductDto product = new ProductDto(1, "Laptop", 101, 49999.99f, 10);

        when(productDao.getProductById(1)).thenReturn(product);
        when(cartService.getCartId("laxman")).thenReturn(101);
        when(cartDao.addItemToCart(101, 1, 2)).thenThrow(new RuntimeException("Insert failed"));

        assertThrows(RuntimeException.class, () -> {
            cartService.addToCart("laxman", 1, 2);
        });
    }
    
    @Test
    public void testRemoveProduct_Success() {
        String username = "laxman";
        int productId = 101;
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeItemFromCart(cartId, productId)).thenReturn(true);

        String result = cartService.removeProduct(username, productId);

        assertEquals("1:Product with ID 101 successfully removed.", result);
    }

    @Test
    public void testRemoveProduct_Failure_RemoveItemFromCart() {
        String username = "laxman";
        int productId = 101;
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeItemFromCart(cartId, productId)).thenReturn(false);

        String result = cartService.removeProduct(username, productId);

        assertEquals("0:Failed in removing Product with ID 101", result);
    }

    @Test
    public void testRemoveProduct_CartCreatedThenRemoved_Success() {
        String username = "laxman";
        int productId = 101;
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(true);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeItemFromCart(cartId, productId)).thenReturn(true);

        String result = cartService.removeProduct(username, productId);

        assertEquals("1:Product with ID 101 successfully removed.", result);
    }

    @Test
    public void testRemoveProduct_CartCreationFails() {
        String username = "laxman";
        int productId = 101;
        int userId = 1;
        int cartId = -1;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.removeItemFromCart(-1, productId)).thenReturn(false);

        String result = cartService.removeProduct(username, productId);

        assertEquals("0:Failed in removing Product with ID 101", result);
    }

    @Test
    public void testRemoveProduct_InvalidUser() {
        String username = "InvalidUser";
        int productId = 101;
        int userId = -1;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.removeItemFromCart(-1, productId)).thenReturn(false);

        String result = cartService.removeProduct(username, productId);

        assertEquals("0:Failed in removing Product with ID 101", result);
    }
    
    @Test
    void testRemoveAllProduct_Success() {
        String username = "laxman";
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeAllItemsFromCart(cartId)).thenReturn(true);

        String result = cartService.removeAllProduct(username);

        assertEquals("1:All products are successfully removed.", result);
    }

    @Test
    void testRemoveAllProduct_Failure_RemoveItems() {
        String username = "laxman";
        int userId = 1;
        int cartId = 10;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeAllItemsFromCart(cartId)).thenReturn(false);

        String result = cartService.removeAllProduct(username);

        assertEquals("0:Failed in removing all products from cart.", result);
    }

    @Test
    void testRemoveAllProduct_CartCreated_Success() {
        String username = "laxman";
        int userId = 1;
        int cartId = 11;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(true);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.removeAllItemsFromCart(cartId)).thenReturn(true);

        String result = cartService.removeAllProduct(username);

        assertEquals("1:All products are successfully removed.", result);
    }

    @Test
    void testRemoveAllProduct_CartCreationFails() {
        String username = "laxman";
        int userId = 1;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.removeAllItemsFromCart(-1)).thenReturn(false);

        String result = cartService.removeAllProduct(username);

        assertEquals("0:Failed in removing all products from cart.", result);
    }

    @Test
    void testRemoveAllProduct_InvalidUser() {
        String username = "InvalidUser";
        int userId = -1;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.removeAllItemsFromCart(-1)).thenReturn(false);

        String result = cartService.removeAllProduct(username);

        assertEquals("0:Failed in removing all products from cart.", result);
    }
    
    @Test
    void testUpdateCart_Success() {
        String username = "laxman";
        int userId = 1;
        int cartId = 101;
        int productId = 5;
        int quantity = 3;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.updateItemInCart(quantity, cartId, productId)).thenReturn(true);

        String result = cartService.updateCart(username, productId, quantity);

        assertEquals("1:Product quanitty successfully updated.", result);
    }

    @Test
    void testUpdateCart_Failure_UpdateFails() {
        String username = "laxman";
        int userId = 1;
        int cartId = 101;
        int productId = 5;
        int quantity = 3;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.updateItemInCart(quantity, cartId, productId)).thenReturn(false);

        String result = cartService.updateCart(username, productId, quantity);

        assertEquals("0:Failed in updating product quantity.", result);
    }

    @Test
    void testUpdateCart_CartCreationNeeded_Success() {
        String username = "laxman";
        int userId = 1;
        int cartId = 202;
        int productId = 5;
        int quantity = 2;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(true);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(cartId);
        when(cartDao.updateItemInCart(quantity, cartId, productId)).thenReturn(true);

        String result = cartService.updateCart(username, productId, quantity);

        assertEquals("1:Product quanitty successfully updated.", result);
    }

    @Test
    void testUpdateCart_CartCreationFails() {
        String username = "laxman";
        int userId = 1;
        int productId = 5;
        int quantity = 2;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.updateItemInCart(quantity, -1, productId)).thenReturn(false);

        String result = cartService.updateCart(username, productId, quantity);

        assertEquals("0:Failed in updating product quantity.", result);
    }

    @Test
    void testUpdateCart_InvalidUser() {
        String username = "InvalidUser";
        int userId = -1;
        int productId = 5;
        int quantity = 2;

        when(userDao.getUserIDByUsername(username)).thenReturn(userId);
        when(cartDao.getCartIDByUserID(userId)).thenReturn(-1);
        when(cartDao.addUserIdToCart(userId)).thenReturn(false);
        when(cartDao.updateItemInCart(quantity, -1, productId)).thenReturn(false);

        String result = cartService.updateCart(username, productId, quantity);

        assertEquals("0:Failed in updating product quantity.", result);
    }
}
