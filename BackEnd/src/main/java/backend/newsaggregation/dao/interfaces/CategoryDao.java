package backend.newsaggregation.dao.interfaces;

import java.util.List;

import backend.newsaggregation.dao.impl.CategoryDaoImpl;
import backend.newsaggregation.model.Category;

public interface CategoryDao {
	static CategoryDao getInstance() {
        return CategoryDaoImpl.getInstance();
    }
	
	boolean addCategory(String name);
	List<Category> getAllCategory();
}
