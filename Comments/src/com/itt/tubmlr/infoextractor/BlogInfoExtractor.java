package com.itt.tubmlr.infoextractor;

import org.json.JSONException;
import org.json.JSONObject;
import com.itt.tubmlr.dto.BlogInfoDto;

public class BlogInfoExtractor {

	public BlogInfoDto extractBlogInfo(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject tumblelog = jsonObject.getJSONObject("tumblelog");

        String title = tumblelog.optString("title", "N/A");
        String description = tumblelog.optString("description", "No description available");
        String name = tumblelog.optString("name", "Unknown");
        int totalPosts = jsonObject.getInt("posts-total");

        return new BlogInfoDto(title, description, name, totalPosts);
    }
}
