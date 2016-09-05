package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


class AddNewList extends AsyncTask<String, Object, JSONObject> {
    private BoardActivity boardActivity;

    public AddNewList(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject listElement = null;
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[0]);
            String query = builder.build().getEncodedQuery();

            URL url = new URL("https://jak.codecamps.de/jak-list/lists/board/" + boardActivity.getAuthToken() + "/" + boardActivity.getActiveBoardId());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));) {
                writer.write(query);
                writer.flush();
                if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStream in = new BufferedInputStream(connection.getInputStream());
                         Scanner s = new Scanner(in).useDelimiter("\\A");) {
                        String response = s.hasNext() ? s.next() : null;
                        listElement = new JSONObject(response);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listElement;
    }

    protected void onPostExecute(JSONObject jsonObject) {
        new UpdateListElements(boardActivity).execute(boardActivity.getActiveBoardId());
    }
}
