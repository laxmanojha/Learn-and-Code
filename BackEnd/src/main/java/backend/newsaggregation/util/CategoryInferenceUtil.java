package backend.newsaggregation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryInferenceUtil {
    private static final Map<String, String> keywordCategoryMap = Map.ofEntries(
        Map.entry("business", "business"),
        Map.entry("tech", "technology"),
        Map.entry("sport", "sports"),
        Map.entry("movie", "entertainment"),
        Map.entry("stock", "business")
    );

    public static List<Integer> inferCategories(String text) {
        List<Integer> categories = new ArrayList<>();
        String lower = text.toLowerCase();

        for (Map.Entry<String, String> entry : keywordCategoryMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                categories.add(resolveCategoryId(entry.getValue()));
            }
        }

        if (categories.isEmpty()) {
            categories.add(resolveCategoryId("general")); // fallback
        }
        return categories;
    }

    private static int resolveCategoryId(String type) {
        // Dummy method. Should query or cache category IDs.
        switch (type.toLowerCase()) {
            case "business": return 1;
            case "technology": return 2;
            case "sports": return 3;
            case "entertainment": return 4;
            case "general": return 5;
            default: return 5;
        }
    }
}