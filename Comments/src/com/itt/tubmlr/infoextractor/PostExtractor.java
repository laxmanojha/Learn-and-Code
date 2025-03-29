package com.itt.tubmlr.infoextractor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class PostExtractor {

    private final JSONArray posts;

    public PostExtractor(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        this.posts = jsonObject.getJSONArray("posts");
    }

    public void displayImageUrls() {
        try {
            for (int postIndex = 0; postIndex < posts.length(); postIndex++) {
                JSONObject post = posts.getJSONObject(postIndex);
                if (post.has("photos")) {
                    displayImageUrls(postIndex, post);
                }
            }
        } catch (JSONException e) {
            System.out.println("Error parsing image URLs: " + e.getMessage());
        }
    }
    
    public void displayImageUrls(int postIndex, JSONObject post) {
    	System.out.println((postIndex + 1) + ".");
        JSONArray photos = post.getJSONArray("photos");
        for (int photoIndex = 0; photoIndex < photos.length(); photoIndex++) {
            System.out.println(photos.getJSONObject(photoIndex).getString("photo-url-1280"));
        }
    }
}
