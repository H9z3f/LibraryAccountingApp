package com.library.client.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpUtility {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DOMAIN = "http://localhost:8080";

    public static String sendGetRequest(String jwt, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(DOMAIN + url))
                .header("Authorization", "Bearer " + jwt)
                .GET()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPostRequest(String jwt, String url, Map<String, String> requestBody) throws Exception {
        String jsonBody = OBJECT_MAPPER.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(DOMAIN + url))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPutRequest(String jwt, String url, Map<String, String> requestBody) throws Exception {
        String jsonBody = OBJECT_MAPPER.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(DOMAIN + url))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPutRequest(String jwt, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(DOMAIN + url))
                .header("Authorization", "Bearer " + jwt)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String sendDeleteRequest(String jwt, String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(DOMAIN + url))
                .header("Authorization", "Bearer " + jwt)
                .DELETE()
                .build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
