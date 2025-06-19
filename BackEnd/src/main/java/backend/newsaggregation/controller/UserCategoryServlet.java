//package backend.newsaggregation.controller;
//
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//
//@WebServlet("/api/category/user")
//public class UserCategoryServlet extends HttpServlet {
//	private final UserCategoryService userCategoryService = new UserCategoryService();
//
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        PrintWriter out = resp.getWriter();
//        String action = req.getParameter("action");
//
//        try {
//            int userId = Integer.parseInt(req.getParameter("userId"));
//            int categoryId = Integer.parseInt(req.getParameter("categoryId"));
//
//            if ("updateCategory".equalsIgnoreCase(action)) {
//                boolean enabled = Boolean.parseBoolean(req.getParameter("enabled"));
//                String keywordInput = req.getParameter("keywords");
//                List<String> keywords = keywordInput != null && !keywordInput.isBlank()
//                        ? Arrays.asList(keywordInput.split(","))
//                        : List.of();
//                userCategoryService.setCategoryWithKeywords(userId, categoryId, enabled, keywords);
//                out.println("Category preference and keywords updated.");
//
//            } else if ("addKeyword".equalsIgnoreCase(action)) {
//                String keyword = req.getParameter("keyword");
//                userCategoryService.addKeyword(userId, categoryId, keyword);
//                out.println("Keyword added.");
//
//            } else if ("updateKeyword".equalsIgnoreCase(action)) {
//                String oldKeyword = req.getParameter("oldKeyword");
//                String newKeyword = req.getParameter("newKeyword");
//                userCategoryService.updateKeyword(userId, categoryId, oldKeyword, newKeyword);
//                out.println("Keyword updated.");
//
//            } else if ("deleteKeyword".equalsIgnoreCase(action)) {
//                String keyword = req.getParameter("keyword");
//                userCategoryService.deleteKeyword(userId, categoryId, keyword);
//                out.println("Keyword deleted.");
//
//            } else {
//                out.println("Invalid action parameter.");
//            }
//        } catch (Exception e) {
//            out.println("Error: " + e.getMessage());
//        }
//    }
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        PrintWriter out = resp.getWriter();
//        try {
//            int userId = Integer.parseInt(req.getParameter("user"));
//            if (req.getParameter("categoryId") != null) {
//                int categoryId = Integer.parseInt(req.getParameter("categoryId"));
//                List<String> keywords = userCategoryService.getKeywords(userId, categoryId);
//                out.println("Keywords for category " + categoryId + ":");
//                for (String keyword : keywords) {
//                    out.println("- " + keyword);
//                }
//            } else {
//                List<UserCategory> categories = userCategoryService.getUserCategories(userId);
//                for (UserCategory uc : categories) {
//                    out.println("Category ID: " + uc.getCategoryId() + " - " + (uc.isEnabled() ? "Enabled" : "Disabled"));
//                }
//            }
//        } catch (Exception e) {
//            out.println("Error: " + e.getMessage());
//        }
//    }
//}
