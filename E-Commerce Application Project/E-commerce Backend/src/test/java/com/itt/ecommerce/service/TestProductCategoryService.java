package com.itt.ecommerce.service;

import com.itt.ecommerce.dao.CategoryDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestProductCategoryService {

    @Test
    public void testGetAllCategories_ReturnsList() {
        List<CategoryDto> mockCategories = Arrays.asList(
                new CategoryDto(1, "Electronics"),
                new CategoryDto(2, "Books")
        );

        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockCategoryDao.getAllCategories()).thenReturn(new ArrayList<>(mockCategories));

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<CategoryDto> result = service.getAllCategories();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Books", result.get(1).getCategory_name());
    }

    @Test
    public void testGetAllCategories_EmptyList() {
        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockCategoryDao.getAllCategories()).thenReturn(new ArrayList<>());

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<CategoryDto> result = service.getAllCategories();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllCategories_NullReturn() {
        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockCategoryDao.getAllCategories()).thenReturn(null);

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<CategoryDto> result = service.getAllCategories();

        Assertions.assertNull(result);
    }

    @Test
    public void testGetProductsByCategory_ReturnsList() {
        List<ProductDto> mockProducts = Arrays.asList(
                new ProductDto(1, "Laptop", 1, 70000.0f, 10),
                new ProductDto(2, "Phone", 1, 30000.0f, 15)
        );

        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockProductDao.getAllProductsByCategoryId(1)).thenReturn(mockProducts);

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<ProductDto> result = service.getProductsByCategory(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Laptop", result.get(0).getProduct_name());
    }

    @Test
    public void testGetProductsByCategory_EmptyList() {
        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockProductDao.getAllProductsByCategoryId(2)).thenReturn(Collections.emptyList());

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<ProductDto> result = service.getProductsByCategory(2);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetProductsByCategory_NullReturn() {
        CategoryDao mockCategoryDao = Mockito.mock(CategoryDao.class);
        ProductDao mockProductDao = Mockito.mock(ProductDao.class);

        Mockito.when(mockProductDao.getAllProductsByCategoryId(3)).thenReturn(null);

        ProductCategoryService service = new ProductCategoryService(mockCategoryDao, mockProductDao);
        List<ProductDto> result = service.getProductsByCategory(3);

        Assertions.assertNull(result);
    }
}
