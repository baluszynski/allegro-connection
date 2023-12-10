package com.example.allegro;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.URLEncoder;


public class AllegroAPIClient {
    private static final String CATEGORIES_URL = "https://api.allegro.pl.allegrosandbox.pl/sale/categories"; //.allegrosandbox.pl    0383ef2b-c318-4e43-af61-2aed743413c3
    private static final String PRODUCTS_URL = "https://api.allegro.pl.allegrosandbox.pl/sale/products";
    private static final String TEST_URL = "https://api.allegro.pl.allegrosandbox.pl/sale/products?phrase=kapsulki";
    private static final String TEST_PRODUKT = "https://api.allegro.pl.allegrosandbox.pl/sale/delivery-methods";
    private static final String BASE_URL = "https://api.allegro.pl.allegrosandbox.pl/offers/listing?";
    private static final String PHRASE = "kapsulki do ekspresow";
    private static final String SMART_OPTION = "SMART";
    private static final String SUPERSELLER_OPTION  = "SUPERSELLER";
    private static final String BRAND_ZONE_OPTION  = "BRAND_ZONE";
    private static final String DELIVERY_METHOD = "5b445fe6580ce26bb2f9960a";
    private static final boolean FALLBACK = true;

    public static String getOffers(String token) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);

            // wyszukiwana fraza zakodowana do formy URL
            urlBuilder.append("phrase=").append(urlEncode(PHRASE));

            // super sprzedawca
            urlBuilder.append("&option=").append(SUPERSELLER_OPTION);

            // strefa marek
            urlBuilder.append("&option=").append(BRAND_ZONE_OPTION);

            // forma dostawy
            urlBuilder.append("&deliveryMethod=").append(DELIVERY_METHOD);

            // oferty smart
            urlBuilder.append("&option=").append(SMART_OPTION);

            // stan produktu
            urlBuilder.append("&parameter.11323=11323_1"); // produkt jest nowy
            urlBuilder.append("&parameter.11323=11323_1223636"); // produkt jest przepakowany

            // hardkodujemy zakup od razu
            urlBuilder.append("&sellingMode.format=BUY_NOW");

            // jeśli dopisane fraza szukana jest nie tylko w tytułach ALE TAKŻE w opisie
            urlBuilder.append("&searchMode=DESCRIPTIONS");

            System.out.println("Final URL: " + urlBuilder.toString());

//            String finalUrl = urlBuilder.toString();
            URL url = new URL(urlBuilder.toString());
//            URL url = new URL(TEST_PRODUKT);
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

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL", e);
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
            String mainCategories = getOffers(refresh_token);
            System.out.println(mainCategories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
