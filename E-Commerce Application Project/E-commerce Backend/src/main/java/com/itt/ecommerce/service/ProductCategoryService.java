package com.itt.ecommerce.service;

import java.util.List;
import com.itt.ecommerce.dao.CategoryDao;
import com.itt.ecommerce.dao.ProductDao;
import com.itt.ecommerce.dto.CategoryDto;
import com.itt.ecommerce.dto.ProductDto;

public class ProductCategoryService {
	
	private CategoryDao categoryDao;
	private ProductDao productDao;
	
	public ProductCategoryService() {
		this(new CategoryDao(), new ProductDao());
	}
	
	public ProductCategoryService(CategoryDao categoryDao, ProductDao productDao) {
		this.categoryDao = categoryDao;
		this.productDao = productDao;
	}
	
	public List<CategoryDto> getAllCategories() {
		List<CategoryDto> allCategories = categoryDao.getAllCategories();
		return allCategories;
	}
	
	public List<ProductDto> getProductsByCategory(int category_id) {
		List<ProductDto> allProducts = productDao.getAllProductsByCategoryId(category_id);
		return allProducts;
	}
}
