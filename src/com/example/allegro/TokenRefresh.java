package com.example.allegro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class TokenRefresh {
    private static final String CLIENT_ID = "b6e830d1676144f78ea9e0b5f850e11c";
    private static final String CLIENT_SECRET = "BTfuNFAtEIYGIMh4Q5Y1euMqCBW2HmBgnQjaL5izWp9SY7y5GeBSQaIqdHiFTARt";
    private static final String REDIRECT_URI = "http://localhost:8000";
    private static final String TOKEN_URL = "https://allegro.pl/auth/oauth/token";

    public static void main(String[] args) {
        BufferedReader reader = null;
        BufferedWriter refreshWriter = null;
        BufferedWriter accessWriter = null;

        try {
            reader = new BufferedReader(new FileReader("refresh_token.txt"));
            String refresh_token = reader.readLine();
            String[] tokens = getNextToken(refresh_token);

            refreshWriter = new BufferedWriter(new FileWriter("refresh_token.txt"));
            refreshWriter.write(tokens[1]);

            accessWriter = new BufferedWriter(new FileWriter("access_token.txt"));
            accessWriter.write(tokens[0]);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (refreshWriter != null) refreshWriter.close();
                if (accessWriter != null) accessWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] getNextToken(String token) throws IOException {
        URL url = new URL(TOKEN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String data = "grant_type=refresh_token&refresh_token=" + token + "&redirect_uri=" + REDIRECT_URI;
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

        Writer writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(data);
        writer.flush();

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        conn.disconnect();

        // Parsing JSON response
        String access_token = "";
        String refresh_token = "";
        String jsonResponse = response.toString();
        if (!jsonResponse.isEmpty()) {
            String[] parts = jsonResponse.split(",");
            for (String part : parts) {
                if (part.contains("\"access_token\"")) {
                    access_token = part.split(":")[1].replaceAll("\"", "").trim();
                } else if (part.contains("\"refresh_token\"")) {
                    refresh_token = part.split(":")[1].replaceAll("\"", "").trim();
                }
            }
        }

        return new String[]{access_token, refresh_token};
    }
}
