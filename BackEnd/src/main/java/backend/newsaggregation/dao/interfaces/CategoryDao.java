package backend.newsaggregation.dao.interfaces;

import backend.newsaggregation.dao.impl.CategoryDaoImpl;

public interface CategoryDao {
	static CategoryDao getInstance() {
        return CategoryDaoImpl.getInstance();
    }
	
	boolean addCategory(String name);
}
