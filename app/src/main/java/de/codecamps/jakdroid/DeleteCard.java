package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.Card;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeleteCard extends AsyncTask<String, Object, String> {
    private String authToken;
    private RecyclerView recyclerView;

    public DeleteCard(String authToken, RecyclerView recyclerView) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "DeleteCard instanciate");

        this.authToken = authToken;
        this.recyclerView = recyclerView;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  Card: %s", params[0]));
        try {
            URL url = new URL("https://jak.codecamps.de/jak-card/cards/" + authToken + "/" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK?params[0]:null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String cardId) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("DeleteCard / onPostExecute  item: %s", cardId));

        super.onPostExecute(cardId);

        CardContentFragment.ContentAdapter adapter = (CardContentFragment.ContentAdapter) recyclerView.getAdapter();
        adapter.remove(cardId);
        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
    }
}
