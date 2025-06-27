package backend.newsaggregation.service;

import java.util.List;

import backend.newsaggregation.dao.interfaces.CategoryDao;
import backend.newsaggregation.model.Category;

public class CategoryService {
	
	private static CategoryService instance;
	private CategoryDao categoryDao;
	
	private CategoryService() {
		this(CategoryDao.getInstance());
	}
	
	private CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public boolean addCategory(String name) throws Exception {
        return categoryDao.addCategory(name);
    }
    
    public List<Category> getAllCategory() {
    	return categoryDao.getAllCategory();
    }
}
