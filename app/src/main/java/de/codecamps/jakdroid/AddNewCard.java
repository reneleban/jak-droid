package de.codecamps.jakdroid;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import de.codecamps.jakdroid.data.Card;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


class AddNewCard extends AsyncTask<String, Object, JSONObject> {
    private BoardActivity boardActivity;

    public AddNewCard(BoardActivity boardActivity) {
        this.boardActivity = boardActivity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject listElement = null;
        try {
            Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", params[1]).appendQueryParameter("description", "");
            String query = builder.build().getEncodedQuery();

            URL url = new URL("https://jak.codecamps.de/jak-card/cards/" + boardActivity.getAuthToken() + "/" + params[0]);
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
        ViewPager viewPager = (ViewPager) boardActivity.findViewById(R.id.viewpager);
        RecyclerView recyclerView = (RecyclerView) viewPager.getFocusedChild();
        CardContentFragment.ContentAdapter adapter = (CardContentFragment.ContentAdapter) recyclerView.getAdapter();
        try {
            adapter.add(new Card(jsonObject));
            adapter.notifyDataSetChanged();
            recyclerView.refreshDrawableState();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
