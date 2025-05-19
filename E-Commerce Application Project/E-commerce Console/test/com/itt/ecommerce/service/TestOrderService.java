package com.itt.ecommerce.service;

import static org.mockito.Mockito.*;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itt.ecommerce.dto.CartItemDto;
import com.itt.ecommerce.util.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class TestOrderService {

    private final Gson gson = new Gson();
    private final String username = "testuser";

    @Test
    public void testCheckOut_withItems() throws IOException, InterruptedException {
        List<CartItemDto> cartItems = List.of(new CartItemDto(1, 2, 3, 1, "Product1", 100f));

        ObjectMapper mapper = new ObjectMapper();
        String expectedPayload = mapper.writeValueAsString(new com.itt.ecommerce.dto.CartDto(cartItems));

        try (
            MockedStatic<CartService> cartServiceMock = mockStatic(CartService.class);
            MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)
        ) {
            cartServiceMock.when(() -> CartService.getCartItems(username))
                           .thenReturn(cartItems);

            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn("{\"success\": true}");

            httpUtilMock.when(() -> HttpUtil.sendPostRequest(contains("/order"), eq(expectedPayload)))
                        .thenReturn(mockResponse);
            httpUtilMock.when(() -> HttpUtil.processResponse(mockResponse, "Order checkout"))
                        .thenCallRealMethod();

            OrderService.checkOut(username);

            cartServiceMock.verify(() -> CartService.getCartItems(username));
        }
    }

    @Test
    public void testCheckOut_withEmptyCart() throws IOException, InterruptedException {
        try (MockedStatic<CartService> cartServiceMock = mockStatic(CartService.class)) {
            cartServiceMock.when(() -> CartService.getCartItems(username)).thenReturn(List.of());

            OrderService.checkOut(username);

            cartServiceMock.verify(() -> CartService.getCartItems(username));
        }
    }

    @Test
    public void testViewOrderHistory_success() throws IOException, InterruptedException {
        String jsonResponse = """
            {
                "success": true,
                "orderHistory": [
                    {
                        "productCategory": "Electronics",
                        "productName": "Mouse",
                        "productPrice": 500.0,
                        "productQuantity": 2,
                        "totalProductsPrice": 1000.0,
                        "orderDate": "2024-04-15"
                    }
                ]
            }
            """;

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(jsonResponse);

            httpUtilMock.when(() -> HttpUtil.sendPostRequest(contains("/order/order-history"), eq("username=" + username)))
                        .thenReturn(mockResponse);

            OrderService.viewOrderHistory(username);

            httpUtilMock.verify(() -> HttpUtil.sendPostRequest(anyString(), eq("username=" + username)));
        }
    }

    @Test
    public void testViewOrderHistory_failure() throws IOException, InterruptedException {
        String errorResponse = "{\"success\": false, \"message\": \"User not found\"}";

        try (MockedStatic<HttpUtil> httpUtilMock = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(400);
            when(mockResponse.body()).thenReturn(errorResponse);

            httpUtilMock.when(() -> HttpUtil.sendPostRequest(contains("/order/order-history"), eq("username=" + username)))
                        .thenReturn(mockResponse);

            OrderService.viewOrderHistory(username);

            httpUtilMock.verify(() -> HttpUtil.sendPostRequest(anyString(), eq("username=" + username)));
        }
    }
}
