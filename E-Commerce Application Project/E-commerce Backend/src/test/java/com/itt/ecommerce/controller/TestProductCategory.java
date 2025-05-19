package com.itt.ecommerce.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import com.itt.ecommerce.service.ProductCategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestProductCategory {

    @InjectMocks
    private ProductCategory productCategory;

    @Mock
    private ProductCategoryService productCategoryService;

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
    public void testFetchAllCategories_Success() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(productCategoryService.getAllCategories())
                .thenReturn(Arrays.asList(new CategoryDto(1, "Electronics")));

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(responseJson.get("success").getAsBoolean());
        assertTrue(responseJson.has("categories"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testFetchAllCategories_EmptyList() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(productCategoryService.getAllCategories()).thenReturn(Collections.emptyList());

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(responseJson.get("success").getAsBoolean());
        assertEquals("Error in fetching all categories.", responseJson.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testFetchProductsByCategory_Success() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/products");
        when(productCategoryService.getProductsByCategory(1))
                .thenReturn(Arrays.asList(new ProductDto(101, "Laptop", 1, 1200.0f, 5)));

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertTrue(responseJson.get("success").getAsBoolean());
        assertTrue(responseJson.has("products"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void testFetchProductsByCategory_NotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/products");
        when(productCategoryService.getProductsByCategory(1)).thenReturn(Collections.emptyList());

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(responseJson.get("success").getAsBoolean());
        assertEquals("No products found for this category.", responseJson.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testInvalidCategoryIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/abc/products");

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(responseJson.get("success").getAsBoolean());
        assertEquals("Invalid category ID format.", responseJson.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testInvalidURLFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/1/invalid");

        productCategory.service(request, response);

        printWriter.flush();
        JsonObject responseJson = JsonParser.parseString(stringWriter.toString()).getAsJsonObject();

        assertFalse(responseJson.get("success").getAsBoolean());
        assertEquals("Invalid URL format.", responseJson.get("message").getAsString());
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
