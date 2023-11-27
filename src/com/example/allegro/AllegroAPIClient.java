package com.example.allegro;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AllegroAPIClient {
    private static final String CLIENT_ID = "0dfb0c28961d4880bcd4c861e111a750";
    private static final String CLIENT_SECRET = "KMysGcDKqgqebfAvmGp6FDsofuorz1pZt0uHaUXz6lKYdEVwEr7qXSKxfd7k2xCD";
    private static final String TOKEN_URL = "https://allegro.pl/auth/oauth/token";
    private static final String CATEGORIES_URL = "https://api.allegro.pl/sale/categories"; //.allegrosandbox.pl
    private static final String PRODUCTS_URL = "https://api.allegro.pl/sale/products";
    private static final String TEST_URL = "https://api.allegro.pl/sale/products?phrase=kapsulki";
    public static String getAccessToken() {
        try {
            URL url = new URL(TOKEN_URL);
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            String data = "grant_type=client_credentials";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(response.toString());
                String name = (String) jsonObject.get("access_token");
                return name;
            } else {
                throw new RuntimeException("Failed to get access token. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMainCategories(String token) {
        try {
            URL url = new URL(TEST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                return response.toString();
            } else {
                throw new RuntimeException("Failed to get main categories. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            TokenRefresh.main(args);
            BufferedReader reader = null;
            String refresh_token = null;
            try {
                reader = new BufferedReader(new FileReader("refresh_token.txt"));
                refresh_token = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String mainCategories = getMainCategories(refresh_token);
            System.out.println(mainCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
