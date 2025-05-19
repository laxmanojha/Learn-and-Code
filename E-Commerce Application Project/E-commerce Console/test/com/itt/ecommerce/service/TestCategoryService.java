package com.itt.ecommerce.service;

import com.google.gson.Gson;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.util.HttpUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCategoryService {

    private final Gson gson = new Gson();

    @Test
    public void testGetAllCategories_success() throws IOException, InterruptedException {
        String jsonResponse = """
            {
              "success": true,
              "categories": [
                { "category_id": 1, "category_name": "Electronics" },
                { "category_id": 2, "category_name": "Books" }
              ]
            }
            """;

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(jsonResponse);

            mockedHttpUtil.when(() -> HttpUtil.sendGetRequest(contains("/category")))
                          .thenReturn(mockResponse);

            List<CategoryDto> categories = CategoryService.getAllCategories();

            assertEquals(2, categories.size());
            assertEquals("Electronics", categories.get(0).getCategory_name());
            assertEquals("Books", categories.get(1).getCategory_name());
        }
    }

    @Test
    public void testGetAllCategories_failure() throws IOException, InterruptedException {
        String errorResponse = "{\"success\": false, \"message\": \"No categories found\"}";

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(400);
            when(mockResponse.body()).thenReturn(errorResponse);

            mockedHttpUtil.when(() -> HttpUtil.sendGetRequest(contains("/category")))
                          .thenReturn(mockResponse);

            List<CategoryDto> categories = CategoryService.getAllCategories();
            assertTrue(categories.isEmpty());
        }
    }

    @Test
    public void testGetAllProductsOfCategory_success() throws IOException, InterruptedException {
        String jsonResponse = """
            {
              "success": true,
              "products": [
                {
                  "product_id": 1,
                  "product_name": "Laptop",
                  "price": 1000.0,
                  "description": "High performance laptop"
                },
                {
                  "product_id": 2,
                  "product_name": "Mouse",
                  "price": 500.0,
                  "description": "Wireless mouse"
                }
              ]
            }
            """;

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(jsonResponse);

            mockedHttpUtil.when(() -> HttpUtil.sendGetRequest(contains("/category/1/products")))
                          .thenReturn(mockResponse);

            List<ProductDto> products = CategoryService.getAllProductsOfCategory(1);

            assertEquals(2, products.size());
            assertEquals("Laptop", products.get(0).getProduct_name());
            assertEquals(500.0, products.get(1).getPrice());
        }
    }

    @Test
    public void testGetAllProductsOfCategory_failure() throws IOException, InterruptedException {
        String errorResponse = "{\"success\": false, \"message\": \"Category not found\"}";

        try (MockedStatic<HttpUtil> mockedHttpUtil = mockStatic(HttpUtil.class)) {
            HttpResponse<String> mockResponse = mock(HttpResponse.class);
            when(mockResponse.statusCode()).thenReturn(404);
            when(mockResponse.body()).thenReturn(errorResponse);

            mockedHttpUtil.when(() -> HttpUtil.sendGetRequest(contains("/category/99/products")))
                          .thenReturn(mockResponse);

            List<ProductDto> products = CategoryService.getAllProductsOfCategory(99);
            assertTrue(products.isEmpty());
        }
    }
}
