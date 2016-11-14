package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


class AddNewBoard extends AsyncTask<String, Object, JSONObject> {
    private BoardActivity boardActivity;

    public AddNewBoard(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject board = null;
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[0]);
            String query = builder.build().getEncodedQuery();

            URL url = new URL("https://jak.codecamps.de/jak-board/board/" + boardActivity.getAuthToken());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
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
                        board = new JSONObject(response);
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
        return board;
    }

    protected void onPostExecute(JSONObject jsonObject) {
        new UpdateBoardList(boardActivity).execute();
    }
}
