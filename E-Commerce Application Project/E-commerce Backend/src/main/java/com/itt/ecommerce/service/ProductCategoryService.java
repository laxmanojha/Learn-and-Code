package com.itt.ecommerce.service;

import java.util.List;
import com.itt.ecommerce.dao.CategoryDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;

public class ProductCategoryService {
	public static List<CategoryDto> getAllCategories() {
		List<CategoryDto> allCategories = CategoryDao.getAllCategories();
		return allCategories;
	}
	
	public static List<ProductDto> getProductsByCategory(int category_id) {
		List<ProductDto> allProducts = ProductDao.getAllProductsByCategoryId(category_id);
		return allProducts;
	}
}
