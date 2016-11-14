package de.codecamps.jakdroid;

import android.os.AsyncTask;
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

public class UpdateCardList extends AsyncTask<String, Object, List<Card>> {
    private String authToken;

    public UpdateCardList(String authToken) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "UpdateCardList instanciate");

        this.authToken = authToken;
    }

    @Override
    protected List<Card> doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  list: %s", params[0]));

        JSONArray cardArray = null;

        try {
            URL url = new URL("https://jak.codecamps.de/jak-card/cards/" + authToken + "/" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection != null && connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     Scanner s = new Scanner(in).useDelimiter("\\A");) {
                    String response = s.hasNext() ? s.next() : null;
                    cardArray = new JSONArray(response);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }

            List<Card> cardList = new ArrayList<>();
            if (cardArray != null) {
                for (int i = 0; i < cardArray.length(); i++) {
                    JSONObject cardElement = cardArray.getJSONObject(i);
                    cardList.add(new Card(cardElement));
                }
            }
            return cardList;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Card> cards) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  items: %s", cards.size()));

        super.onPostExecute(cards);
    }
}
