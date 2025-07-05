package backend.newsaggregation.dao.interfaces;

import java.util.List;
import backend.newsaggregation.dao.impl.HiddenKeywordDaoImpl;
import backend.newsaggregation.model.HiddenKeyword;

public interface HiddenKeywordDao {
	
	static HiddenKeywordDao getInstance() {
		return HiddenKeywordDaoImpl.getInstance();
	}
	
    boolean addKeyword(String keyword);
    boolean deleteKeyword(int id);
    List<HiddenKeyword> getAllKeywords();
}
