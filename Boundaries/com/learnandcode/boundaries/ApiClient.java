package com.learnandcode.boundaries;

import java.io.IOException;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiClient {
	
	public static GeoLocationDto getGeoLocationData(String cityName) throws IOException, InterruptedException {
		GeoLocationDto geoLocationDto = null;
		
		String url = getRequestUrl(cityName);
		HttpResponse<String> response = HttpUtil.sendGetRequest(url);
		geoLocationDto = getGeoLocationData(response);
	    
	    return geoLocationDto;
	}
	
	private static String getRequestUrl(String cityName) {
		String url = ApiConstants.BASE_URL.getValue() + ApiConstants.URL_QUERY.getValue() + cityName + ApiConstants.API_ID.getValue() + ApiConstants.API_KEY.getValue();
		return url;
	}
	
	private static GeoLocationDto getGeoLocationData(HttpResponse<String> response) {
		GeoLocationDto geoLocationDto = null;
		String responseBody = response.body();
		
	    JSONArray arr = new JSONArray(responseBody);
	    for (int index = 0; index < arr.length(); index++) {
	        JSONObject obj = arr.getJSONObject(index);
	        geoLocationDto = new GeoLocationDto(obj.getString("name"), obj.getString("country"), obj.getString("state"), obj.getDouble("lat"), obj.getDouble("lon"));
	    }
	    
	    return geoLocationDto;
	}
}
