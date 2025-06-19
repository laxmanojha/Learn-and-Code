//package backend.newsaggregation.dao.impl;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//
//import backend.newsaggregation.dao.interfaces.UserCategoryDao;
//import backend.newsaggregation.util.DatabaseConfig;
//
//public class UserCategoryDaoImpl implements UserCategoryDao{
//	
//	private static UserCategoryDaoImpl instance;
//	Connection conn = DatabaseConfig.getConnection();
//
//    private UserCategoryDaoImpl() {}
//
//    public static UserCategoryDaoImpl getInstance() {
//        if (instance == null) {
//            instance = new UserCategoryDaoImpl();
//        }
//        return instance;
//    }
//
//    public void enableCategoryWithKeywords(int userId, int categoryId, List<String> keywords){
//        String deleteSQL = "DELETE FROM notification_category_pref WHERE user_id = ? AND category_id = ?";
//        try {
//        	
//            PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
//            deleteStmt.setInt(1, userId);
//            deleteStmt.setInt(2, categoryId);
//            deleteStmt.executeUpdate();
//        }
//
//        String insertSQL = "INSERT INTO user_category_keywords (user_id, category_id, keyword) VALUES (?, ?, ?)";
//        try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
//            for (String keyword : keywords) {
//                insertStmt.setInt(1, userId);
//                insertStmt.setInt(2, categoryId);
//                insertStmt.setString(3, keyword.trim());
//                insertStmt.addBatch();
//            }
//            insertStmt.executeBatch();
//        }
//    }
//
//    public void disableCategory(int userId, int categoryId) throws SQLException {
//        String sql = "DELETE FROM user_category_keywords WHERE user_id = ? AND category_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, userId);
//            stmt.setInt(2, categoryId);
//            stmt.executeUpdate();
//        }
//    }
//
//    public List<UserCategory> getAllByUserId(int userId) throws SQLException {
//        List<UserCategory> list = new ArrayList<>();
//        String sql = "SELECT c.id AS category_id, c.name, uck.user_id AS exists " +
//                     "FROM categories c LEFT JOIN (" +
//                     "  SELECT DISTINCT category_id, user_id FROM user_category_keywords WHERE user_id = ?" +
//                     ") uck ON c.id = uck.category_id";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, userId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                boolean enabled = rs.getObject("exists") != null;
//                list.add(new UserCategory(
//                    userId,
//                    rs.getInt("category_id"),
//                    enabled
//                ));
//            }
//        }
//        return list;
//    }
//
//    public List<String> getKeywordsByUserAndCategory(int userId, int categoryId) throws SQLException {
//        List<String> keywords = new ArrayList<>();
//        String sql = "SELECT keyword FROM user_category_keywords WHERE user_id = ? AND category_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, userId);
//            stmt.setInt(2, categoryId);
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                keywords.add(rs.getString("keyword"));
//            }
//        }
//        return keywords;
//    }
//
//    public void addKeyword(int userId, int categoryId, String keyword) throws SQLException {
//        String sql = "INSERT INTO user_category_keywords (user_id, category_id, keyword) VALUES (?, ?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, userId);
//            stmt.setInt(2, categoryId);
//            stmt.setString(3, keyword);
//            stmt.executeUpdate();
//        }
//    }
//
//    public void updateKeyword(int userId, int categoryId, String oldKeyword, String newKeyword) throws SQLException {
//        String sql = "UPDATE user_category_keywords SET keyword = ? WHERE user_id = ? AND category_id = ? AND keyword = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, newKeyword);
//            stmt.setInt(2, userId);
//            stmt.setInt(3, categoryId);
//            stmt.setString(4, oldKeyword);
//            stmt.executeUpdate();
//        }
//    }
//
//    public void deleteKeyword(int userId, int categoryId, String keyword) throws SQLException {
//        String sql = "DELETE FROM user_category_keywords WHERE user_id = ? AND category_id = ? AND keyword = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, userId);
//            stmt.setInt(2, categoryId);
//            stmt.setString(3, keyword);
//            stmt.executeUpdate();
//        }
//    }
//
//    public void close() throws SQLException {
//        if (connection != null && !connection.isClosed()) {
//            connection.close();
//        }
//    }
//}
