package de.codecamps.jakdroid.auth;

import android.net.Uri;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class JAKDroidServerAuthenticate implements ServerAuthenticate {
    @Override
    public String userSignUp(String name, String pass, String authTokenType) throws Exception {
        String authToken = null;
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("username", name)
                .appendQueryParameter("password", pass);
        String query = builder.build().getEncodedQuery();

        URL url = new URL("https://jak.codecamps.de/jak-login/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));) {
            writer.write(query);
            writer.flush();
            connection.connect();
            authToken = getTokenFromResponse(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authToken;
    }

    @Override
    public String userSignIn(String name, String pass, String authTokenType) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://jak.codecamps.de/jak-login/login").openConnection();
        String encoded = Base64.encodeToString((name + ":" + pass).getBytes(), Base64.NO_WRAP);
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        connection.setRequestMethod("GET");
        return getTokenFromResponse(connection);
    }

    private String getTokenFromResponse(HttpURLConnection connection) throws IOException {
        String authToken = null;
        if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 Scanner s = new Scanner(in).useDelimiter("\\A");) {
                String response = s.hasNext() ? s.next() : null;
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("token")) {
                    authToken = jsonObject.getString("token");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return authToken;
    }
}
