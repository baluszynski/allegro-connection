//package com.example.allegro;
//
//import java.io.*;
//import java.net.*;
//import java.util.Base64;
//import org.json.*;
//import org.json.simple.JSONObject;
//
//public class AllegroAuth {
//    private static final String CLIENT_ID = "b6e830d1676144f78ea9e0b5f850e11c";
//    private static final String CLIENT_SECRET = "BTfuNFAtEIYGIMh4Q5Y1euMqCBW2HmBgnQjaL5izWp9SY7y5GeBSQaIqdHiFTARt";
//    private static final String CODE_URL = "https://allegro.pl/auth/oauth/device";
//    private static final String TOKEN_URL = "https://allegro.pl/auth/oauth/token";
//
//    public static JSONObject sendPostRequest(String urlString, String payload) throws IOException, JSONException {
//        URL url = new URL(urlString);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//        String auth = CLIENT_ID + ":" + CLIENT_SECRET;
//        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
//
//        try (OutputStream os = conn.getOutputStream()) {
//            byte[] input = payload.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
//        StringBuilder response = new StringBuilder();
//        String responseLine;
//        while ((responseLine = br.readLine()) != null) {
//            response.append(responseLine.trim());
//        }
//
//        return new JSONObject(response.toString());
//    }
//
//    public static JSONObject get_code() throws IOException, JSONException {
//        String payload = "client_id=" + CLIENT_ID;
//        return sendPostRequest(CODE_URL, payload);
//    }
//
//    public static JSONObject get_access_token(String device_code) throws IOException, JSONException {
//        String data = "grant_type=urn:ietf:params:oauth:grant-type:device_code&device_code=" + device_code;
//        return sendPostRequest(TOKEN_URL, data);
//    }
//
//    public static String await_for_access_token(int interval, String device_code) throws IOException, JSONException, InterruptedException {
//        while (true) {
//            Thread.sleep(interval * 1000);
//            JSONObject tokenResponse = get_access_token(device_code);
//            if (tokenResponse.getInt("status_code") == 400) {
//                if ("slow_down".equals(tokenResponse.getString("error"))) {
//                    interval += 5;
//                } else if ("access_denied".equals(tokenResponse.getString("error"))) {
//                    break;
//                }
//            } else {
//                return tokenResponse.getString("access_token");
//            }
//        }
//        return null;
//    }
//
//    public static void main(String[] args) {
//        try {
//            JSONObject codeResponse = get_code();
//            System.out.println(codeResponse.toString());
//
//            // Zakomentowane dla bezpieczeństwa, ale można je odkomentować dla pełnej funkcjonalności
//            // String deviceCode = codeResponse.getString("device_code");
//            // int interval = codeResponse.getInt("interval");
//            // String accessToken = await_for_access_token(interval, deviceCode);
//            // System.out.println("access_token = " + accessToken);
//        } catch (IOException | JSONException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}