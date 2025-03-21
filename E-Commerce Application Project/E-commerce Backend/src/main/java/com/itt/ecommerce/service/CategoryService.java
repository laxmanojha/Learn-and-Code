package com.itt.ecommerce.service;

import java.util.ArrayList;
import com.itt.ecommerce.dao.CategoryDao;
import com.itt.ecommerce.dto.CategoryDto;

public class CategoryService {
	public static ArrayList<CategoryDto> getAllCategories() {
		ArrayList<CategoryDto> allCategories = new ArrayList<CategoryDto>();
		allCategories = CategoryDao.getAllCategories();
		return allCategories;
	}
}
