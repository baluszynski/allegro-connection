package com.example.allegro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static spark.Spark.*;

public class AuthorizationCodeReceiver {

    private static final String CLIENT_ID = "0dfb0c28961d4880bcd4c861e111a750";  // Wprowadź Client_ID aplikacji
    private static final String CLIENT_SECRET = "KMysGcDKqgqebfAvmGp6FDsofuorz1pZt0uHaUXz6lKYdEVwEr7qXSKxfd7k2xCD";  // Wprowadź Client_Secret aplikacji
    private static final String REDIRECT_URI = "http://localhost:8000";  // Wprowadź redirect_uri
    private static final String TOKEN_URL = "https://allegro.pl/auth/oauth/token";
    private static final String PRODUCTS_URL = "https://api.allegro.pl/sale/products?phrase=kapsulki+do+kawy";

    public static void main(String[] args) {
        port(8000); // Ustawia port serwera na 8000
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        get("/", (req, res) -> {
            System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
            String code = req.queryParams("code");
            if (code != null && !code.isEmpty()) {
                System.out.println("Przechwycony kod dostępu: " + code);
                String accessToken = getAccessToken(code);
                System.out.println("Access Token: " + accessToken);
                String response = allegroTokenRequest(accessToken);
                System.out.println("Response: " + response);
//                String mainCategories = getMainCategories(accessToken);
//                System.out.println(mainCategories);
                return "Kod dostępu został przechwycony pomyślnie!";
            } else {
                String authorizationUrl = getAuthorizationCodeUrl();
                return "Zaloguj się do Allegro - skorzystaj z URL w swojej przeglądarce:<br>" + authorizationUrl;
            }
        });
    }
    public static String allegroTokenRequest(String code) throws Exception {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String authString = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedAuthString = java.util.Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
//        String codeVerifier = "twój_kod_weryfikujący"; // opcjonalny, jeśli korzystasz z PKCE
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] codeVerifierBytes = new byte[32]; // Możesz dostosować długość, aby spełnić wymagania Allegro
//        secureRandom.nextBytes(codeVerifierBytes);
//        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifierBytes);
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
////        byte[] codeVerifierBytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
//        digest.update(codeVerifierBytes);
//        byte[] codeChallengeBytes = digest.digest();
//        String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(codeChallengeBytes);


        String requestBody = "grant_type=authorization_code"
                + "&code=" + code
                + "&redirect_uri=" + REDIRECT_URI;
//                + "&code_verifier=" + codeVerifier; // opcjonalny, jeśli korzystasz z PKCE


        connection.setRequestProperty("Authorization", "Basic " + encodedAuthString);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
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
            System.out.println("Response: " + response.toString());
            return response.toString();
        } else {
            throw new RuntimeException("Failed to get access token. Response Code: " + responseCode);
        }
    }
    public static String getAuthorizationCodeUrl() {
        String AUTH_URL = "https://allegro.pl/auth/oauth/authorize";
        return AUTH_URL + "?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
    }

    public static String getAccessToken(String authorizationCode) {
        try {
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            URL url = new URL(TOKEN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            String data = "grant_type=authorization_code&code=" + authorizationCode + "&redirect_uri=" + REDIRECT_URI;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                return response.toString();
            } else {
                throw new RuntimeException("Failed to get access token. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMainCategories(String token) {
        try {
//            URI uri = new URI(PRODUCTS_URL + "?phrase=kapsulki+do+kawy");
            URL url = new URL(PRODUCTS_URL);
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
}