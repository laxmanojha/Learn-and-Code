package com.itt.tubmlr;

import java.util.Scanner;
import java.net.http.HttpResponse;
import com.itt.tubmlr.util.ApiHandler;
import com.itt.tubmlr.dto.BlogInfoDto;
import com.itt.tubmlr.util.UserInputHandler;
import com.itt.tubmlr.infoextractor.PostExtractor;
import com.itt.tubmlr.infoextractor.BlogInfoExtractor;

public class Main {

    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
        ApiHandler apiHandler = null;
        BlogInfoExtractor blogInfoExtractor = null;
        PostExtractor postExtractor = null;
        UserInputHandler userInputHandler = null;
        String blogName = null;
        int[] range = {};
        
        while (true) {
        	try {
        		userInputHandler = new UserInputHandler();
        		blogName = userInputHandler.getBlogName(scanner);
        		range = userInputHandler.getPostRange(scanner);
        		break;
        	} catch (IllegalArgumentException e) {
        		System.out.println(e.getMessage());
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        	}
        }
        
        try {
        	apiHandler = new ApiHandler();
        	HttpResponse<String> response = apiHandler.sendGetRequest(blogName, range[1] - range[0], range[0]);
        	String jsonResponse = apiHandler.parseResponse(response);
        	
        	blogInfoExtractor = new BlogInfoExtractor();
        	BlogInfoDto blogDetails = blogInfoExtractor.extractBlogInfo(jsonResponse);
        	blogDetails.displayInfo();

        	postExtractor = new PostExtractor(jsonResponse);
            postExtractor.displayImageUrls();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
