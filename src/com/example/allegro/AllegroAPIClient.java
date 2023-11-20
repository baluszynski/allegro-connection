package com.example.allegro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private static final String TEST_URL = "https://api.allegro.pl/sale/products?phrase=termos";
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
            String token = getAccessToken();
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println(token);
            String test = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiIzNDI1MTcxMyIsInNjb3BlIjpbImFsbGVncm86YXBpOnNhbGU6c2V0dGluZ3M6cmVhZCIsImFsbGVncm86YXBpOnNhbGU6b2ZmZXJzOnJlYWQiXSwiYWxsZWdyb19hcGkiOnRydWUsImlzcyI6Imh0dHBzOi8vYWxsZWdyby5wbCIsImV4cCI6MTcwMDQ2Mzk3OSwianRpIjoiODAyY2FiMTctODlmMC00NGZhLTg0ODMtZDkzZWNjZGVmMjE4IiwiY2xpZW50X2lkIjoiYjZlODMwZDE2NzYxNDRmNzhlYTllMGI1Zjg1MGUxMWMifQ.xOIu9MWBhN5VLpsSYozj4tvEczokutqUbeZDAjaf-jZFDJAROWapWh8pfYcTs0KL-I_CqOUFFGatkOT5uYeKPy9uy9NwS_S45I6UY_Pzga5l4CC-0AqO1MbWqK_zZ8s-Xri8ucwy8AOVJThsOrSz45mmF4qcXQwZVo2_-hJTT6BvQL8-E1I5HaT2A-o8mDHmulAe2QXkAzy1YbBCmHjEHjHCYw6H3mGEMMUToS4vwq-f144Yx-ddxgDkxUauVKDads5L7hZc8rFZoXM3drzwJGvTmS5FugEAbFRY02unJKmhvaKFtvtYXCjGd2PhbeatnQY5eS-rrC0XoSKIVUA6Lg";
            String mainCategories = getMainCategories(test);
            System.out.println(mainCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
