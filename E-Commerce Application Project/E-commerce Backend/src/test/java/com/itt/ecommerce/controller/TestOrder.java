package com.itt.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.CartDto;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.dto.OrderHistoryDto;
import com.itt.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestOrder {

    @InjectMocks
    private Order order;

    @Mock
    private OrderService orderService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testCompleteOrder_Success() throws Exception {
        CartItemDto item = new CartItemDto(101, 0, 0, 0, "Laptop", 2);
        CartDto cartDto = new CartDto(Arrays.asList(item));

        String cartJson = new ObjectMapper().writeValueAsString(cartDto);

        BufferedReader reader = new BufferedReader(new StringReader(cartJson));
        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(reader);
        when(orderService.completeOrder(anyList())).thenReturn("1:Order placed successfully");

        order.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertEquals("Order placed successfully", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testCompleteOrder_Failure() throws Exception {
        CartItemDto item = new CartItemDto(102, 0, 0, 0, "Phone", 1);
        CartDto cartDto = new CartDto(Arrays.asList(item));

        String cartJson = new ObjectMapper().writeValueAsString(cartDto);

        BufferedReader reader = new BufferedReader(new StringReader(cartJson));
        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(reader);
        when(orderService.completeOrder(anyList())).thenReturn("0:Order could not be completed");

        order.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Order could not be completed", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testOrderHistory_Success() throws Exception {
        when(request.getPathInfo()).thenReturn("/order-history");
        when(request.getParameter("username")).thenReturn("john");
        when(orderService.getOrderHistory("john")).thenReturn(
                Arrays.asList(new OrderHistoryDto())
        );

        order.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(json.get("success").getAsBoolean());
        assertTrue(json.has("orderHistory"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testOrderHistory_Empty() throws Exception {
        when(request.getPathInfo()).thenReturn("/order-history");
        when(request.getParameter("username")).thenReturn("john");
        when(orderService.getOrderHistory("john")).thenReturn(Collections.emptyList());

        order.service(request, response);

        printWriter.flush();
        JsonObject json = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(json.get("success").getAsBoolean());
        assertEquals("Error in fetching order history.", json.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testInvalidPathInfo() throws Exception {
        when(request.getPathInfo()).thenReturn("/unknown");

        order.service(request, response);

        printWriter.flush();
        assertNotNull(stringWriter.toString());
    }
}
