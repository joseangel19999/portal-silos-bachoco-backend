package com.bachoco.persistence.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpUrlConnectionClient {
	  private final String baseUrl;
	    private final String authHeader;

	    public HttpUrlConnectionClient(String baseUrl, String username, String password) {
	        this.baseUrl = baseUrl;
	        String creds = username + ":" + password;
	        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
	    }

	    public String get(String endpointWithParams) throws IOException {
	        URL url = new URL(baseUrl + endpointWithParams);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("Authorization", authHeader);
	        con.setRequestProperty("Accept", "application/json");
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");

	        int status = con.getResponseCode();
	        if (status != 200) {
	            throw new IOException("HTTP Error: " + status);
	        }

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
	            StringBuilder response = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                response.append(line);
	            }
	            return response.toString();
	        } finally {
	            con.disconnect();
	        }
	    }
}
