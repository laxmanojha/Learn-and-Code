//package backend.newsaggregation.service;
//
//public class UserCategoryService {
//	private final DatabaseConnection dbConnection;
//
//    public UserCategoryService() {
//        this.dbConnection = new MySQLDatabaseConnection();
//    }
//
//    public void setCategoryWithKeywords(int userId, int categoryId, boolean enabled, List<String> keywords) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            if (enabled) {
//                dao.enableCategoryWithKeywords(userId, categoryId, keywords);
//            } else {
//                dao.disableCategory(userId, categoryId);
//            }
//        } finally {
//            dao.close();
//        }
//    }
//
//    public List<UserCategory> getUserCategories(int userId) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            return dao.getAllByUserId(userId);
//        } finally {
//            dao.close();
//        }
//    }
//
//    public List<String> getKeywords(int userId, int categoryId) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            return dao.getKeywordsByUserAndCategory(userId, categoryId);
//        } finally {
//            dao.close();
//        }
//    }
//
//    public void addKeyword(int userId, int categoryId, String keyword) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            dao.addKeyword(userId, categoryId, keyword);
//        } finally {
//            dao.close();
//        }
//    }
//
//    public void updateKeyword(int userId, int categoryId, String oldKeyword, String newKeyword) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            dao.updateKeyword(userId, categoryId, oldKeyword, newKeyword);
//        } finally {
//            dao.close();
//        }
//    }
//
//    public void deleteKeyword(int userId, int categoryId, String keyword) throws Exception {
//        UserCategoryDAO dao = new UserCategoryDAO(dbConnection);
//        try {
//            dao.deleteKeyword(userId, categoryId, keyword);
//        } finally {
//            dao.close();
//        }
//    }
//}
