package com.itt.ecommerce.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.service.CartService;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCartServlet {

    @InjectMocks
    private Cart cartServlet;

    @Mock
    private CartService cartService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void testFetchCartItemsSuccess() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        List<CartItemDto> mockCartItems = List.of(new CartItemDto());
        when(cartService.fetchAllCartItems("john")).thenReturn(mockCartItems);

        cartServlet.doGet(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertTrue(json.get("success").getAsBoolean());
        assertTrue(json.has("cartItems"));
    }

    @Test
    void testFetchCartItemsEmpty() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        when(cartService.fetchAllCartItems("john")).thenReturn(Collections.emptyList());

        cartServlet.doGet(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Cart is empty for this user.", json.get("message").getAsString());
    }

    @Test
    void testAddToCartSuccess() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("2");
        when(cartService.addToCart("john", 1, 2)).thenReturn("1:Product added to cart");

        cartServlet.doPost(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertTrue(json.get("success").getAsBoolean());
    }

    @Test
    void testAddToCartInvalidInput() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        when(request.getParameter("productId")).thenReturn("abc");
        when(request.getParameter("quantity")).thenReturn("2");

        cartServlet.doPost(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Invalid productId or quantity", json.get("message").getAsString());
    }

    @Test
    void testUpdateCartSuccess() throws Exception {
        String body = "{\"username\": \"john\", \"productId\": 1, \"quantity\": 5}";
        BufferedReader reader = new BufferedReader(new StringReader(body));
        when(request.getReader()).thenReturn(reader);
        when(cartService.updateCart("john", 1, 5)).thenReturn("1:Quantity updated");

        cartServlet.doPut(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertTrue(json.get("success").getAsBoolean());
        assertEquals("Quantity updated", json.get("message").getAsString());
    }

    @Test
    void testRemoveProductFromCart() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        when(request.getPathInfo()).thenReturn("/1");
        when(cartService.removeProduct("john", 1)).thenReturn("1:Product removed");

        cartServlet.doDelete(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertTrue(json.get("success").getAsBoolean());
        assertEquals("Product removed", json.get("message").getAsString());
    }

    @Test
    void testClearCart() throws Exception {
        when(request.getParameter("username")).thenReturn("john");
        when(request.getPathInfo()).thenReturn(null);
        when(cartService.removeAllProduct("john")).thenReturn("1:Cart cleared");

        cartServlet.doDelete(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertTrue(json.get("success").getAsBoolean());
        assertEquals("Cart cleared", json.get("message").getAsString());
    }

    @Test
    void testDeleteCartMissingUsername() throws Exception {
        when(request.getParameter("username")).thenReturn(null);

        cartServlet.doDelete(request, response);
        writer.flush();

        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();
        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Username is required", json.get("message").getAsString());
    }
}
