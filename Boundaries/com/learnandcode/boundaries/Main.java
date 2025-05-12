package com.learnandcode.boundaries;

import java.util.Scanner;
import java.io.IOException;
import com.learnandcode.boundaries.util.ApiClient;
import com.learnandcode.boundaries.dto.GeoLocationDto;

public class Main {

    public static void main(String[] args) {
        try {
        	String requestedData = "";
			String cityName = getUserInput();
			GeoLocationDto geoLocationDto = ApiClient.getGeoLocationData(cityName);
			if (geoLocationDto != null)
				requestedData = geoLocationDto.toString();
			else
				requestedData = "Entered city is not found!";
			System.out.println(requestedData);
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private static String getUserInput() {
    	String cityName = null;
    	try(Scanner sc = new Scanner(System.in)) {
    		System.out.print("Please enter city name: ");
			cityName = sc.nextLine();
			if (cityName.contains(" ")) {
				cityName = cityName.replace(' ', '-');
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return cityName;
    }
}
