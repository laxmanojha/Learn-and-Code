package com.itt.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.HttpUtil;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

class TestCartService {

    private static final Gson gson = new Gson();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    private void setScannerInput(String data) throws Exception {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        Field scannerField = CartService.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(null, new Scanner(in));
    }

    @Test
    void testAddToCart_WithValidInput() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"success\":true,\"message\":\"Item added\"}");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            mockedHttpUtil.when(() -> HttpUtil.sendPostRequest(any(), any())).thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Add to Cart")).thenCallRealMethod();

            assertDoesNotThrow(() -> CartService.addToCart("john", 1, 1001, 2));
        }
    }

    @Test
    void testShowCategories_PrintsCorrectFormat() {
        List<CategoryDto> categories = Arrays.asList(
                new CategoryDto(2, "Books"),
                new CategoryDto(1, "Electronics")
        );

        CartService.showCategories(categories);
        String output = outContent.toString();

        assertTrue(output.contains("CategoryID"));
        assertTrue(output.contains("Electronics"));
        assertTrue(output.contains("Books"));
    }

    @Test
    void testShowProducts_PrintsCorrectFormat() {
        List<ProductDto> products = Arrays.asList(
                new ProductDto(2, "Book", 10, 120.0f, 10),
                new ProductDto(1, "Phone", 10, 599.99f, 5)
        );

        CartService.showProducts(products);
        String output = outContent.toString();

        assertTrue(output.contains("ProductID"));
        assertTrue(output.contains("Phone"));
        assertTrue(output.contains("Book"));
    }

    @Test
    void testParseCartResponse_WithValidData() {
        String json = """
            {
                "success": true,
                "cartItems": [
                    {
                        "cartItemId": 1,
                        "cartId": 1,
                        "productId": 1001,
                        "quantity": 2,
                        "productName": "Phone",
                        "productPrice": 599.99
                    }
                ]
            }
        """;

        List<CartItemDto> items = invokeParseCartResponse(json);
        assertEquals(1, items.size());
        assertEquals("Phone", items.get(0).getProductName());
    }

    @Test
    void testViewCart_DisplaysEmptyCart() throws Exception {
        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            mockedHttpUtil.when(() -> HttpUtil.sendGetRequest(any())).thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn("{\"success\":true,\"cartItems\":[]}");

            CartService.viewCart("john");
            String output = outContent.toString();
            assertTrue(output.contains("Your cart is empty."));
        }
    }

    @Test
    void testUpdateCart_Success() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"success\":true,\"message\":\"Cart updated\"}");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            mockedHttpUtil.when(() -> HttpUtil.sendPutRequest(any(), any())).thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Cart update")).thenCallRealMethod();

            assertDoesNotThrow(() -> CartService.updateCart("john", 1001, 5));
        }
    }

    @Test
    void testRemoveProduct_Success() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"success\":true,\"message\":\"Product removed\"}");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            mockedHttpUtil.when(() -> HttpUtil.sendDeleteRequest(any())).thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "Product removal")).thenCallRealMethod();

            assertDoesNotThrow(() -> CartService.removeProduct("john", 1001));
        }
    }

    @Test
    void testRemoveAllProduct_Success() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"success\":true,\"message\":\"All items removed\"}");

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            mockedHttpUtil.when(() -> HttpUtil.sendDeleteRequest(any())).thenReturn(mockResponse);
            mockedHttpUtil.when(() -> HttpUtil.processResponse(mockResponse, "All Products removeal")).thenCallRealMethod();

            assertDoesNotThrow(() -> CartService.removeAllProduct("john"));
        }
    }

    @Test
    void testShowCartItems_NonEmptyList() {
        List<CartItemDto> items = List.of(
                new CartItemDto(1, 1, 1001, 2, "Laptop", 1000.0f)
        );
        CartService.showCartItems(items);
        String output = outContent.toString();
        assertTrue(output.contains("Laptop"));
        assertTrue(output.contains("2000.00"));
    }

    private List<CartItemDto> invokeParseCartResponse(String json) {
        try {
            Method method = CartService.class.getDeclaredMethod("parseCartResponse", String.class);
            method.setAccessible(true);
            return (List<CartItemDto>) method.invoke(null, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
