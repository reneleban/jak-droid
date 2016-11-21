package de.codecamps.jakdroid;

import android.os.AsyncTask;
import android.util.Log;
import de.codecamps.jakdroid.auth.AccountGeneral;
import de.codecamps.jakdroid.data.Card;
import de.codecamps.jakdroid.helpers.AsyncTaskHelpers;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class UpdateCardList extends AsyncTask<String, Object, List<Card>> {
    private String authToken;

    public UpdateCardList(String authToken) {
        Log.d(AccountGeneral.ACCOUNT_NAME, "UpdateCardList instantiate");

        this.authToken = authToken;
    }

    @Override
    protected List<Card> doInBackground(String... params) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  list: %s", params[0]));
        try {
            return AsyncTaskHelpers.retrieveCards(authToken, params[0]);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Card> cards) {
        Log.d(AccountGeneral.ACCOUNT_NAME, String.format("UpdateCardList / doInBackground  items: %s", cards.size()));

        super.onPostExecute(cards);
    }
}
