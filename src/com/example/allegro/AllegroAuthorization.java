package com.example.allegro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import static spark.Spark.*;

public class AllegroAuthorization {

    private static final String CLIENT_ID = "0dfb0c28961d4880bcd4c861e111a750";  // Wprowadź Client_ID aplikacji
    private static final String CLIENT_SECRET = "KMysGcDKqgqebfAvmGp6FDsofuorz1pZt0uHaUXz6lKYdEVwEr7qXSKxfd7k2xCD";  // Wprowadź Client_Secret aplikacji
    private static final String REDIRECT_URI = "http://localhost:8000";  // Wprowadź redirect_uri
    private static final String AUTH_URL = "https://allegro.pl/auth/oauth/authorize";
    private static final String TOKEN_URL = "https://allegro.pl/auth/oauth/token";

    public static String getAuthorizationCodeUrl() {
        return AUTH_URL + "?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI;
    }

    public static String getAuthorizationCode() throws IOException {
        String authorizationUrl = getAuthorizationCodeUrl();//WcE4Y9vQQ84iXnbNmi1ToX8nVZ4udRUL
        System.out.println("Zaloguj do Allegro - skorzystaj z URL w swojej przeglądarce oraz wprowadź authorization code ze zwróconego URL:");
        System.out.println("---  " + authorizationUrl + "  ---");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("code: ");
        return reader.readLine();
    }

    public static String getAccessToken(String authorizationCode) throws IOException {
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
    public static String testGet() {
        port(8000); // Ustawia port serwera na 8000
        String authorizationCode = null;
        try {
            authorizationCode = getAuthorizationCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String accessTokenResponse = null;
        try {
            accessTokenResponse = getAccessToken(authorizationCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        get("/", (req, res) -> {
            String code = req.queryParams("code");
            if (code != null && !code.isEmpty()) {
                // Tutaj masz dostęp do przechwyconego kodu dostępu
                System.out.println("Przechwycony kod dostępu: " + code);

                // Tutaj możesz wykonać operacje na przechwyconym kodzie dostępu
                // np. wywołanie metody getAccessToken(code) itp.
//                String authorizationCode = getAuthorizationCode();
//                String accessTokenResponse = getAccessToken(authorizationCode);

                // Poniżej możesz przetworzyć odpowiedź dotyczącą dostępu do tokena w odpowiedni sposób
//                System.out.println("Access Token Response: " + accessTokenResponse);
                // Tu możesz zakończyć serwer lub wyświetlić odpowiedni komunikat dla użytkownika
                return "Kod dostępu został przechwycony pomyślnie!";
            }
            return "Oczekiwanie na kod dostępu...";
        });
        return "xd";
    }
    public static void main(String[] args) {
        try {
            String authorizationCode = getAuthorizationCode();
            String accessTokenResponse = getAccessToken(authorizationCode);
            String test = testGet();
//            // Poniżej możesz przetworzyć odpowiedź dotyczącą dostępu do tokena w odpowiedni sposób
//            System.out.println("Access Token Response: " + accessTokenResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
