package com.learnandcode.boundaries;

import java.io.IOException;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
			System.out.print("Please enter city name: ");
			String cityName = sc.nextLine();
			String url = "http://api.openweathermap.org/geo/1.0/direct?q="+ cityName +"&appid=1b1f64195680003a720629d3c36bab3a";
			try {
			    String response = HttpUtil.sendGetRequest(url).body();
			    JSONArray arr = new JSONArray(response);
			    for (int i = 0; i < arr.length(); i++) {
			        JSONObject obj = arr.getJSONObject(i);
			        System.out.println("Name: " + obj.getString("name"));
			        System.out.println("Country: " + obj.getString("country"));
			        System.out.println("State: " + obj.getString("state"));
			        System.out.println("Latitude: " + obj.getDouble("lat"));
			        System.out.println("Longitude: " + obj.getDouble("lon"));
			    }
   
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (InterruptedException e) {
			    e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
