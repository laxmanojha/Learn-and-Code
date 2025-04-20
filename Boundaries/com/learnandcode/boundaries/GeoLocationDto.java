package com.learnandcode.boundaries;

public class GeoLocationDto {
	private String city;
	private String state;
	private String countryCode;
	private double latitude;
	private double longitude;
	
	public GeoLocationDto(String city, String state, String countryCode, double latitude, double longitude) {
		this.city = city;
		this.state = state;
		this.countryCode = countryCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getCity() {
		return city;
	}
	public String getState() {
		return state;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "GeoLocationData: \ncity = " + city + "\nstate = " + state + "\ncountryCode = " + countryCode + "\nlatitude = "
				+ latitude + "\nlongitude = " + longitude;
	}
}
