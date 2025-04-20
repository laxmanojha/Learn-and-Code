package com.learnandcode.boundaries;

public enum ApiConstants {
    BASE_URL("http://api.openweathermap.org/geo/1.0/direct"),
    URL_QUERY("?q="),
    API_ID("&appid="),
    API_KEY("1b1f64195680003a720629d3c36bab3a");

    private final String value;

    ApiConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
