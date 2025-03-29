package com.itt.tubmlr;

import java.util.Scanner;
import java.net.http.HttpResponse;
import com.itt.tubmlr.util.ApiHandler;
import com.itt.tubmlr.dto.ApiRequestParamsDto;
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
        boolean askInputAgain = true;
        
        while (askInputAgain) {
        	try {
        		userInputHandler = new UserInputHandler();
        		blogName = userInputHandler.getBlogName(scanner);
        		range = userInputHandler.getPostRange(scanner);
        		askInputAgain = false;
        	} catch (IllegalArgumentException e) {
        		System.out.println(e.getMessage());
        	} catch (Exception e) {
        		System.out.println(e.getMessage());
        	}
        }
        
        try {
        	apiHandler = new ApiHandler();
        	/*In below constructor:
        	 * first argument is url.
        	 * second argument is query parameter num(i.e. number of posts)
        	 * third argument is query parameter start(i.e. posts reading start point)*/
        	ApiRequestParamsDto apiRequestParamsDto = new ApiRequestParamsDto(blogName, range[1] - range[0] + 1, range[0]);
        	HttpResponse<String> response = apiHandler.sendGetRequest(apiRequestParamsDto);
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
