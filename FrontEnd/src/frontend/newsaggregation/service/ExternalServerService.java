package frontend.newsaggregation.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import frontend.newsaggregation.model.ExternalServer;
import frontend.newsaggregation.util.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExternalServerService {

    private static ExternalServerService instance; 

    public static ExternalServerService getInstance() {
        if (instance == null) {
            instance = new ExternalServerService();
        }
        return instance;
    }

    public List<ExternalServer> getAllServers() {
    	String url = "http://localhost:8080/backend/api/servers/";
        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);

            if (response.statusCode() == 200) {
                String json = response.body();
                Gson gson = new Gson();

                Type listType = new TypeToken<List<ExternalServer>>() {}.getType();
                return gson.fromJson(json, listType);
            } else {
                System.out.println("Failed to fetch external servers. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while fetching external servers: " + e.getMessage());
        }

        return Collections.emptyList();
    }
    
    public List<ExternalServer> getServerDetails() {
        String url = "http://localhost:8080/backend/api/servers/details";

        try {
            HttpResponse<String> response = HttpUtil.sendGetRequest(url);
            System.out.println(response.body());
            if (response.statusCode() == 200) {
                String json = response.body();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ExternalServer>>() {}.getType();
                return gson.fromJson(json, listType);
            } else {
                System.out.println("Failed to fetch server details. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception while fetching server details: " + e.getMessage());
        }

        return Collections.emptyList();
    }
    
    public boolean updateApiKey(int serverId, String newApiKey) {
        String url = "http://localhost:8080/backend/api/servers/" + serverId;

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("apiKey", newApiKey);

            HttpResponse<String> response = HttpUtil.sendPutRequest(url, requestBody.toString());
            return HttpUtil.processResponse(response, "Update API Key");
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to update server: " + e.getMessage());
            return false;
        }
    }

    public String formatStatus(String status) {
        return "active".equalsIgnoreCase(status) ? "Active" : "Not Active";
    }

    public String formatDate(Date date) {
        return new SimpleDateFormat("dd MMM yyyy").format(date);
    }
}
